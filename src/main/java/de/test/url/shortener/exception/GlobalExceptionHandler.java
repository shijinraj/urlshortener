package de.test.url.shortener.exception;

import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

/**
 * Global Exception Handler
 * 
 * @author Shijin Raj
 * 
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	/**
	 * @param exception
	 * @return ResponseEntity<ErrorDetails>
	 */
	@ExceptionHandler(value = { IllegalArgumentException.class })
	public ResponseEntity<ErrorDetails> handleIllegalArgumentException(final IllegalArgumentException exception) {

		ErrorDetails error = ErrorDetails.builder().code(new Random().nextInt() & Integer.MAX_VALUE)
				.type(HttpStatus.BAD_REQUEST.getReasonPhrase()).description(ExceptionHandlerConstants.INVALID_PARAMETER)
				.moreInfo(

						(exception.getMessage() != null && !exception.getMessage().isEmpty()) ? exception.getMessage()
								: exception.getLocalizedMessage())
				.build();

		return new ResponseEntity<ErrorDetails>(error, HttpStatus.BAD_REQUEST);
	}

	/**
	 * @param nullPointerException
	 * @return ResponseEntity<ErrorDetails>
	 */
	@ExceptionHandler(value = { NullPointerException.class })
	public ResponseEntity<ErrorDetails> handleNullPointerException(final NullPointerException nullPointerException) {
		log.info("Started excecuting handleNullPointerException() : GlobalExceptionHandler");
		ErrorDetails error = ErrorDetails.builder().code(new Random().nextInt() & Integer.MAX_VALUE)
				.type(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
				.description(ExceptionHandlerConstants.CONTACT_SUPPORTTEAM).moreInfo(

						(nullPointerException.getMessage() != null && !nullPointerException.getMessage().isEmpty())
								? nullPointerException.getMessage()
								: nullPointerException.getLocalizedMessage())
				.build();

		return new ResponseEntity<ErrorDetails>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * @param exception
	 * @return ResponseEntity<ErrorDetails>
	 */
	@ExceptionHandler(value = { Exception.class })
	public ResponseEntity<ErrorDetails> handleGeneralException(final Exception exception) {
		ErrorDetails error = ErrorDetails.builder().code(new Random().nextInt() & Integer.MAX_VALUE)
				.type(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
				.description(ExceptionHandlerConstants.CONTACT_SUPPORTTEAM)
				.moreInfo((exception.getMessage() != null && !exception.getMessage().isEmpty()) ? exception.getMessage()
						: exception.getLocalizedMessage())
				.build();

		return new ResponseEntity<ErrorDetails>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * @param exception
	 * @return ResponseEntity<ErrorDetails>
	 */
	@ExceptionHandler(value = { AccessDeniedException.class })
	public ResponseEntity<ErrorDetails> handleAccessDeniedException(final AccessDeniedException exception) {

		ErrorDetails error = ErrorDetails.builder().code(new Random().nextInt() & Integer.MAX_VALUE)
				.type(HttpStatus.UNAUTHORIZED.getReasonPhrase())
				.description(ExceptionHandlerConstants.CONTACT_SUPPORTTEAM)
				.moreInfo((exception.getMessage() != null && !exception.getMessage().isEmpty()) ? exception.getMessage()
						: exception.getLocalizedMessage())
				.build();

		return new ResponseEntity<ErrorDetails>(error, HttpStatus.UNAUTHORIZED);
	}

}
