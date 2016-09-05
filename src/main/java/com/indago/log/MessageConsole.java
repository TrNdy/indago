package com.indago.log;
import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/*
 *  Create a simple console to display text messages.
 *
 *  Messages can be directed here from different sources. Each source can
 *  have its messages displayed in a different color.
 *
 *  Messages can either be appended to the console or inserted as the first
 *  line of the console
 *
 *  You can limit the number of lines to hold in the Document.
 */
public class MessageConsole {

	private static MessageConsole singleton;

	private final JTextComponent textComponent;
	private final Document document;
	private final boolean isAppend;
	private DocumentListener limitLinesListener;

	private final ConsoleOutputStream cosHeader;
	private final ConsoleOutputStream cosTrace;
	private final ConsoleOutputStream cosDebug;
	private final ConsoleOutputStream cosInfo;
	private final ConsoleOutputStream cosWarn;
	private final ConsoleOutputStream cosError;
	private final PrintStream headerStream;
	private final PrintStream traceStream;
	private final PrintStream debugStream;
	private final PrintStream infoStream;
	private final PrintStream warnStream;
	private final PrintStream errorStream;

	public MessageConsole( final JTextComponent textComponent ) {
		this(textComponent, true);
	}

	/*
	 *	Use the text component specified as a simply console to display
	 *  text messages.
	 *
	 *  The messages can either be appended to the end of the console or
	 *  inserted as the first line of the console.
	 */
	public MessageConsole( final JTextComponent textComponent, final boolean isAppend ) {
		singleton = this;

		this.textComponent = textComponent;
		this.textComponent.setFont( new Font( "monospaced", Font.PLAIN, 10 ) );
		this.document = textComponent.getDocument();
		this.isAppend = isAppend;
		textComponent.setEditable( false );

		cosHeader = new ConsoleOutputStream( Color.GRAY, null, true );
		headerStream = new PrintStream( cosHeader, true );

		cosTrace = new ConsoleOutputStream( Color.GRAY, null, false );
		traceStream = new PrintStream( cosTrace, true );
		cosDebug = new ConsoleOutputStream( Color.GREEN, null, false );
		debugStream = new PrintStream( cosDebug, true );
		cosInfo = new ConsoleOutputStream( Color.BLACK, null, false );
		infoStream = new PrintStream( cosInfo, true );
		cosWarn = new ConsoleOutputStream( Color.ORANGE, null, false );
		warnStream = new PrintStream( cosWarn, true );
		cosError = new ConsoleOutputStream( Color.RED, null, false );
		errorStream = new PrintStream( cosError, true );
	}

	/*
	 *  Redirect the output from the standard output to the console
	 *  using the default text color and null PrintStream
	 */
	public void redirectOut()
	{
		redirectOut(null, null);
	}

	/*
	 *  Redirect the output from the standard output to the console
	 *  using the specified color and PrintStream. When a PrintStream
	 *  is specified the message will be added to the Document before
	 *  it is also written to the PrintStream.
	 */
	public void redirectOut(final Color textColor, final PrintStream printStream)
	{
		final ConsoleOutputStream cos = new ConsoleOutputStream( textColor, printStream, true );
		final OutputStream out = new OutputStream() {

			private final PrintStream redirected = new PrintStream( cos, true );
			private final PrintStream original = new PrintStream( System.out );

			@Override
			public void write( final int b ) throws IOException {
				redirected.print( String.valueOf( ( char ) b ) );
				original.print( String.valueOf( ( char ) b ) );
			}

			@Override
			public void write( final byte[] b, final int off, final int len ) throws IOException {
				redirected.print( new String( b, off, len ) );
				original.print( new String( b, off, len ) );
			}

			@Override
			public void write( final byte[] b ) throws IOException {
				write( b, 0, b.length );
			}
		};
		System.setOut( new PrintStream( out, true ) );
	}

	/*
	 *  Redirect the output from the standard error to the console
	 *  using the default text color and null PrintStream
	 */
	public void redirectErr()
	{
		redirectErr(null, null);
	}

	/*
	 *  Redirect the output from the standard error to the console
	 *  using the specified color and PrintStream. When a PrintStream
	 *  is specified the message will be added to the Document before
	 *  it is also written to the PrintStream.
	 */
	public void redirectErr(final Color textColor, final PrintStream printStream)
	{
		final ConsoleOutputStream cos = new ConsoleOutputStream( textColor, printStream, true );
		final OutputStream err = new OutputStream() {

			private final PrintStream redirected = new PrintStream( cos, true );
			private final PrintStream original = new PrintStream( System.err );

			@Override
			public void write( final int b ) throws IOException {
				redirected.print( String.valueOf( ( char ) b ) );
				original.print( String.valueOf( ( char ) b ) );
			}

			@Override
			public void write( final byte[] b, final int off, final int len ) throws IOException {
				redirected.print( new String( b, off, len ) );
				original.print( new String( b, off, len ) );
			}

			@Override
			public void write( final byte[] b ) throws IOException {
				write( b, 0, b.length );
			}
		};
		System.setErr( new PrintStream( err, true ) );
	}

	/*
	 *  To prevent memory from being used up you can control the number of
	 *  lines to display in the console
	 *
	 *  This number can be dynamically changed, but the console will only
	 *  be updated the next time the Document is updated.
	 */
	public void setMessageLines(final int lines)
	{
		if (limitLinesListener != null)
			document.removeDocumentListener( limitLinesListener );

		limitLinesListener = new LimitLinesDocumentListener(lines, isAppend);
		document.addDocumentListener( limitLinesListener );
	}

	/*
	 *	Class to intercept output from a PrintStream and add it to a Document.
	 *  The output can optionally be redirected to a different PrintStream.
	 *  The text displayed in the Document can be color coded to indicate
	 *  the output source.
	 */
	class ConsoleOutputStream extends ByteArrayOutputStream
	{
		private final String EOL = System.getProperty("line.separator");
		private SimpleAttributeSet attributes;
		private final PrintStream printStream;
		private final StringBuffer buffer = new StringBuffer(80);
		private boolean isFirstLine;
		private final boolean addNewlines;

		/*
		 *  Specify the option text color and PrintStream
		 */
		public ConsoleOutputStream( final Color textColor, final PrintStream printStream, final boolean addNewlines )
		{
			if (textColor != null)
			{
				attributes = new SimpleAttributeSet();
				StyleConstants.setForeground(attributes, textColor);
			}

			this.printStream = printStream;

			if (isAppend)
				this.isFirstLine = true;
			this.addNewlines = addNewlines;
		}

		/*
		 *  Override this method to intercept the output text. Each line of text
		 *  output will actually involve invoking this method twice:
		 *
		 *  a) for the actual text message
		 *  b) for the newLine string
		 *
		 *  The message will be treated differently depending on whether the line
		 *  will be appended or inserted into the Document
		 */
		@Override
		public void flush()
		{
			final String message = toString();

			if (message.length() == 0) return;

			if (isAppend)
			    handleAppend(message);
			else
			    handleInsert(message);

			reset();
		}

		/*
		 *	We don't want to have blank lines in the Document. The first line
		 *  added will simply be the message. For additional lines it will be:
		 *
		 *  newLine + message
		 */
		private void handleAppend(final String message)
		{
			//  This check is needed in case the text in the Document has been
			//	cleared. The buffer may contain the EOL string from the previous
			//  message.

			if (document.getLength() == 0)
				buffer.setLength(0);

			if (EOL.equals(message))
			{
				buffer.append(message);
			}
			else
			{
				buffer.append(message);
				clearBuffer();
			}

		}
		/*
		 *  We don't want to merge the new message with the existing message
		 *  so the line will be inserted as:
		 *
		 *  message + newLine
		 */
		private void handleInsert(final String message)
		{
			buffer.append(message);

			if (EOL.equals(message))
			{
				clearBuffer();
			}
		}

		/*
		 *  The message and the newLine have been added to the buffer in the
		 *  appropriate order so we can now update the Document and send the
		 *  text to the optional PrintStream.
		 */
		private void clearBuffer()
		{
			//  In case both the standard out and standard err are being redirected
			//  we need to insert a newline character for the first line only

			if ( addNewlines && isFirstLine && document.getLength() != 0 )
			{
			    buffer.insert(0, "\n");
			}

			isFirstLine = false;
			final String line = buffer.toString();

			try
			{
				if (isAppend)
				{
					final int offset = document.getLength();
					document.insertString(offset, line, attributes);
					textComponent.setCaretPosition( document.getLength() );
				}
				else
				{
					document.insertString(0, line, attributes);
					textComponent.setCaretPosition( 0 );
				}
			}
			catch (final BadLocationException ble) {}

			if (printStream != null)
			{
				printStream.print(line);
			}

			buffer.setLength(0);
		}
	}

	/**
	 * @param messageHeader
	 */
	public void header( final String header ) {
		headerStream.print( header );
	}

	/**
	 * @param message
	 * @param t
	 */
	public void debug( final String message ) {
		debugStream.print( message );
	}

	/**
	 * @param message
	 * @param t
	 */
	public void error( final String message ) {
		errorStream.print( message );
	}

	/**
	 * @param message
	 * @param t
	 */
	public void info( final String message ) {
		infoStream.print( message );
	}

	/**
	 * @param message
	 * @param t
	 */
	public void trace( final String message ) {
		traceStream.print( message );
	}

	/**
	 * @param message
	 * @param t
	 */
	public void warn( final String message ) {
		warnStream.print( message );
	}

	/**
	 * @return
	 */
	public static MessageConsole getInstance() {
		return singleton;
	}

}