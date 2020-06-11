package edu.northeastern.ccs.im;

import edu.northeastern.ccs.im.server.utils.JsonMakerImp;
import edu.northeastern.ccs.im.server.utils.JsonParserImpl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class is similar to the java.io.PrintWriter class, but this class's
 * methods work with our non-blocking Socket classes. This class could easily be
 * made to wait for network output (e.g., be made &quot;non-blocking&quot; in
 * technical parlance), but I have not worried about it yet.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 4.0
 * International License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-sa/4.0/. It is based on work
 * originally written by Matthew Hertz and has been adapted for use in a class
 * assignment at Northeastern University.
 * 
 * @version 1.4
 */
public class NetworkConnection implements Iterable<Message> {

	/** The size of the incoming buffer. */
	private static final int BUFFER_SIZE = 64 * 1024;

	/** The default character set. */
	private static final String CHARSET_NAME = "us-ascii";

	/**
	 * Number of times to try sending a message before we give up in frustration.
	 */
	private static final int MAXIMUM_TRIES_SENDING = 100;

	/** The byte indicating the start of a message */
	private static final byte START_BYTE = 0x7d;

	/** The byte indicating an escape in a message */
	private static final byte ESC_BYTE = 0x7e;

	/** The byte indicating the end of a message */
	private static final byte END_BYTE = 0x7f;

	/** Channel over which we will send and receive messages. */
	private final SocketChannel channel;

	/** Selector for this client's connection. */
	private Selector selector;

	/** Selection key for this client's connection. */
	private SelectionKey key;

	/** Byte buffer to use for incoming messages to this client. */
	private ByteBuffer buff;

	/** Queue of messages for this client. */
	private Queue<Message> messages;

	/**
	 * Creates a new instance of this class. Since, by definition, this class sends
	 * output over the network, we need to supply the non-blocking Socket instance
	 * to which we will write.
	 * 
	 * @param sockChan Non-blocking SocketChannel instance to which we will send all
	 *                 communication.
	 * @throws IOException Exception thrown if we have trouble completing this
     *                     connection
	 */
	public NetworkConnection(SocketChannel sockChan) {
		// Create the queue that will hold the messages received from over the network
		messages = new ConcurrentLinkedQueue<>();
		// Allocate the buffer we will use to read data
		buff = ByteBuffer.allocate(BUFFER_SIZE);
		// Remember the channel that we will be using.
	   // Set up the SocketChannel over which we will communicate.
		channel = sockChan;
		try {
			channel.configureBlocking(false);
			// Open the selector to handle our non-blocking I/O
			selector = Selector.open();
			// Register our channel to receive alerts to complete the connection
			key = channel.register(selector, SelectionKey.OP_READ);
		} catch (IOException e) {
			// For the moment we are going to simply cover up that there was a problem.
			ChatLogger.error(e.toString());
			assert false;
		}
	}


	/**
	 * Send a messages over the network. This method performs its actions by printing
	 * the given messages over the SocketNB instance with which the PrintNetNB was
	 * instantiated. This returns whether our attempt to send the message was
	 * successful.
	 * 
	 * @param msg messages to be sent out over the network.
	 * @return True if we successfully send this message; false otherwise.
	 */
	public boolean sendMessage(Message msg) {
		String json = JsonMakerImp.makeJson(msg);
		ChatLogger.info("Sending: "+json);
		return sendMessage(json);
	}

	public boolean sendMessage(String str) {
		boolean result = true;
		ByteBuffer wrapper = ByteBuffer.wrap(encodeString(str));
		int bytesWritten = 0;
		int attemptsRemaining = MAXIMUM_TRIES_SENDING;
		while (result && wrapper.hasRemaining() && (attemptsRemaining > 0)) {
			try {
				attemptsRemaining--;
				bytesWritten += channel.write(wrapper);
			} catch (IOException e) {
				// Show that this was unsuccessful
				result = false;
			}
		}
		// Check to see if we were successful in our attempt to write the message
		if (result && wrapper.hasRemaining()) {
			ChatLogger.warning("WARNING: Sent only " + bytesWritten + " out of " + wrapper.limit()
					+ " bytes -- dropping this user.");
			result = false;
		}
		return result;
	}

	/**
	 * Encodes the string into a byte array with proper end and start characters,
	 * as well as escaping the proper characters in the string.
	 * @param str the string to be sent over the network.
	 * @return the byte array to send over the network.
	 */
	private byte[] encodeString(String str) {
		byte[] bytes = new byte[2 * str.length() + 2];
		// insert start byte
		bytes[0] = START_BYTE;
		int ind = 1;
		for(byte b : str.getBytes()) {
			if (b == ESC_BYTE || b == END_BYTE) {
				bytes[ind] = ESC_BYTE;
				ind++;
			}
			bytes[ind] = b;
			ind++;
		}
		bytes[ind] = END_BYTE;
		return Arrays.copyOfRange(bytes, 0, ind+1);
	}

	/**
	 * Close this client network connection.
	 */
	public void close() {
		try {
			selector.close();
			channel.close();
		} catch (IOException e) {
			ChatLogger.error("Caught exception: " + e.toString());
			assert false;
		}
	}
	
	@Override
	public Iterator<Message> iterator() {
		return new MessageIterator();
	}

	  /**
	   * Private class that helps iterate over a Network Connection.
	   * 
	   * @author Riya Nadkarni
	   * @version 12-27-2018
	   */
	  private class MessageIterator implements Iterator<Message> {

	    /** Default constructor. */
	    public MessageIterator() {
	      // nothing to do here
	    }

	    @Override
	    public boolean hasNext() {
	      boolean result = false;
	        try {
	            // If we have messages waiting for us, return true.
	            if (!messages.isEmpty()) {
	                result = true;
	            }
	            // Otherwise, check if we can read in at least one new message
	            else if (selector.selectNow() != 0) {
	                assert key.isReadable();
	                // READ in the next set of commands from the channel.
	                channel.read(buff);
	                selector.selectedKeys().remove(key);
	                buff.flip();
	                // Create a decoder which will convert our traffic to something useful
	                Charset charset = Charset.forName(CHARSET_NAME);
	                CharsetDecoder decoder = charset.newDecoder();
	                // Convert the buffer to a format that we can actually use.
	                CharBuffer charBuffer = decoder.decode(buff);
									// get rid of any extra whitespace at the beginning
									// Start scanning the buffer for any and all messages.
									int start = 0;
									// Scan through the entire buffer; check that we have the minimum message size
									while (start < charBuffer.limit()) {
										// If this is not the first message, skip extra space.
										if (start != 0) {
											charBuffer.position(start);
										}

										// Search for starting character, go to next character if not this one.
										if (charBuffer.get(start) != START_BYTE) {
											start++;
											continue;
										}

										String json = decodeMessage(charBuffer);
										// Add this message into our queue
										ChatLogger.info(json);
										Message newMsg = JsonParserImpl.makeMessageFromJSON(json);
										messages.add(newMsg);
										// And move the position to the start of the next character
										start = Math.min(charBuffer.position() + 1, charBuffer.limit());
									}
									// Move any read messages out of the buffer so that we can add to the end.
									buff.position(start);
									// Move all of the remaining data to the start of the buffer.
									buff.compact();
									result = true;
	            }
	        } catch (IOException ioe) {
	            // For the moment, we will cover up this exception and hope it never occurs.
	            assert false;
	        }
	        // Do we now have any messages?
	        return result;
	    }
	    
	    @Override
	    public Message next() {
	      if (messages.isEmpty()) {
	        throw new NoSuchElementException("No next line has been typed in at the keyboard");
	      }
	      Message msg = messages.remove();
	      ChatLogger.info(msg.toString());
	      return msg;
	    }

			/**
			 * Decodes the charBuffer to a string with escape and start/end marker bytes
			 * removed. IE, the original string sent by the client.
			 * @param charBuffer the charBuffer containing the chars received.
			 * @return the string sent over the network.
			 */
			private String decodeMessage(CharBuffer charBuffer) {
				int pos = charBuffer.position();
				int len = 1;
				boolean foundEnd = false;
				StringBuilder jsonBuilder = new StringBuilder();

				while(charBuffer.position() + len < charBuffer.limit()) {
					char c = charBuffer.get(pos + len);

					if (c == ESC_BYTE) {
						len++;
					} else if (c == END_BYTE) {
						foundEnd = true;
						break;
					}

					jsonBuilder.append(charBuffer.get(pos + len));
					len++;
				}

				if(!foundEnd) {
					return null;
				}

				charBuffer.position(pos + len + 1);
				return jsonBuilder.toString();
			}
	  }
}
