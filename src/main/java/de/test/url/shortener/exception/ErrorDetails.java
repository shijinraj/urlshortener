package de.test.url.shortener.exception;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * This class is to show the error details
 * 
 * @author Shijin Raj
 * 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -518401726540019135L;

	/**
	 * errorCode
	 */
	@JsonProperty("error_code")
	private long code;

	/**
	 * error_Type
	 */
	@JsonProperty("error_type")
	private String type;

	/**
	 * error_Description
	 */
	@JsonProperty("error_description")
	private String description;

	/**
	 * more_Info
	 */
	@JsonProperty("more_info")
	private String moreInfo;

}
