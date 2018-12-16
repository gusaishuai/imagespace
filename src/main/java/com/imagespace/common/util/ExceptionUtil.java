package com.imagespace.common.util;

/**
 * 错误信息堆栈，用于快速排查问题
 * @author gusaishuai
 * @since 2015-8-11
 */
public class ExceptionUtil {
	
	/**
	 * 记录错误信息：错误信息+错误信息类+错误信息三行堆栈
	 */
	public static String getExceptionTrace(Throwable e) {
		return getExceptionTrace(e, 3);
	}

	/**
	 * 记录错误信息：错误信息+错误信息类+错误信息N行堆栈
	 * <br>当n<=0时，输出所有堆栈
	 */
	public static String getExceptionTrace(Throwable e, int n) {
		if (e == null) {
			return "exception null";
		}
		try {
			StringBuilder msg = new StringBuilder();
			//错误信息
			String eMessage = e.getMessage();
			//错误信息所属的类
			String eClass = e.getClass().getSimpleName();
			msg.append(eMessage).append(" [ ").append(eClass);
			//可以追踪到那个类的第几行出了问题
			StackTraceElement[] stacks = e.getStackTrace();
			if (stacks.length == 0) {
				return "stack trace null";
			}
			if (n <= 0) {
				n = stacks.length;
			}
			int i=0;
			for (StackTraceElement stack : stacks) {
				if (i >= n) {
					break;
				}
				String fileName = stack.getFileName();
				int lineNumber = stack.getLineNumber();
				if (i == 0) {
					msg.append(" @ ");
				} else {
					msg.append(" -> ");
				}
				msg.append(fileName).append(":").append(lineNumber);
				i++;
			}
			msg.append(" ]");
			return msg.toString();
		} catch (Throwable t) {
			return "";
		}
	}
	
}
