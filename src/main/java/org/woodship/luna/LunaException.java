package org.woodship.luna;

/**
 * 抛出该子类的异常，会自动被LunaErrorHandler 处理
 * @author 老崔
 *
 */
public  class LunaException extends RuntimeException {
	private static final long serialVersionUID = -1096970905084026855L;

	public LunaException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LunaException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public LunaException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public LunaException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
	
}
