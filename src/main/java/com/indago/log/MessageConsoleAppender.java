package com.indago.log;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

public class MessageConsoleAppender extends AppenderSkeleton {

	private MessageConsole console = null;

	/**
	 *
	 */
	public MessageConsoleAppender() {
		this.setLayout( new PatternLayout( "%d{HH:mm:ss} %-6p %-4L:%-25C{1} " ) );
	}

	/**
	 * @see org.apache.log4j.Appender#close()
	 */
	@Override
	public void close() {
	}

	/**
	 * @see org.apache.log4j.Appender#requiresLayout()
	 */
	@Override
	public boolean requiresLayout() {
		return false;
	}

	/**
	 * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
	 */
	@Override
	protected void append( final LoggingEvent event ) {
		if ( console == null ) {
			console = MessageConsole.getInstance();
		}

		if ( console != null ) {
			// This hack is needed to filter all the DEBUG level output coming from org.scijava.log.slf4j.SLF4JLogService:67 -- TODO needs to change on their end!
			if ( event.getLocationInformation().getClassName().equals( "org.scijava.log.slf4j.SLF4JLogService" ) ) { return; }

			String messageHeader = "" + event.getTimeStamp() + " - " + event.getLocationInformation().getClassName() + " - " + event
					.getLocationInformation()
					.getLineNumber() + " - ";
			if (getLayout() != null) {
				messageHeader = getLayout().format( event );
			}

			final String message = event.getRenderedMessage() + "\n";

			console.header( messageHeader );
			if ( event.getLevel().equals( Level.TRACE ) ) {
				console.trace( message );
			}
			if ( event.getLevel().equals( Level.DEBUG ) ) {
				console.debug( message );
			}
			if ( event.getLevel().equals( Level.INFO ) ) {
				console.info( message );
			}
			if ( event.getLevel().equals( Level.WARN ) ) {
				console.warn( message );
			}
			if ( event.getLevel().equals( Level.ERROR ) ) {
				console.error( message );
			}
		}
	}
}
