package com.game.error;

public class TargetNotFoundException extends RuntimeException{
	public TargetNotFoundException() {}
		public TargetNotFoundException(String msg) {
			super(msg);
		}
}
