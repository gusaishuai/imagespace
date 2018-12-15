package com.imagespace.sql.service.common;

import org.apache.commons.lang3.StringUtils;

public class XSqlPagination {

	/**
	 * 当前页数
	 */
	private int pageNo = 1;
	/**
	 * 每页展示
	 */
	private int pageSize = 20;
	private int totalCount;

	public XSqlPagination(int currentPage, int totalCount) {
		if (currentPage < 1) {
			currentPage = 1;
		}
		this.pageNo = currentPage;
		this.totalCount = totalCount;
	}

	public int getStart() {
		if (pageNo > 0) {
			return (pageNo - 1) * pageSize;
		} else {
			return 0;
		}
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageSize() {
		return pageSize;
	}

	public String getPageHtml(String url) {
		if (StringUtils.isBlank(url)) {
			return "";
		}
		if (!url.contains("?")) {
			url += "?pageNo=";
		} else {
			url += "&amp;pageNo=";
		}
		long totalPage;// 总页数
		totalPage = totalCount / pageSize;
		if ((totalCount - totalPage * pageSize) > 0) {
			totalPage = totalPage + 1;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("	<table style=\"height:35px\">\n");
		sb.append("		<tr>\n");
		sb.append("			<td style=\"border:none\">\n");
		sb.append("				<div style=\"position:fixed;right:15px;bottom:15px\">共 ").append(totalCount).append(" 条记录 | 第 ")
				.append(pageNo).append(" 页 | 每页 ").append(pageSize).append(" 条记录 | ");
		if (pageNo > 1) {
			sb.append("<a href=\"").append(url).append("1\">首页</a>").append(" | ")
					.append("<a href=\"").append(url).append(pageNo - 1).append("\" ").append(">上一页</a>");
		} else {
			sb.append("首页 | 上一页");
		}
		sb.append(" | ");
		if (pageNo < totalPage) {
			sb.append("<a href=\"").append(url).append(pageNo + 1).append("\"").append(">下一页</a>").append(" | ")
					.append("<a href=\"").append(url).append(totalPage).append("\"").append(">尾页</a>\n");
		} else {
			sb.append("下一页 | 尾页\n");
		}
		sb.append("				</div>");
		sb.append("			</td>");
		sb.append("		</tr>");
		sb.append("	</table>");
		return sb.toString();
	}

}
