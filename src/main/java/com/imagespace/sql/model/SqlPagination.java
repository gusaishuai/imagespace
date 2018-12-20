package com.imagespace.sql.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SqlPagination {

	/**
	 * 当前页数
	 */
	private int pageNo;
	/**
	 * 每页展示
	 */
	private int pageSize = 20;
	private int totalCount;

	public SqlPagination(int currentPage, int totalCount) {
		this.pageNo = currentPage < 1 ? 1 : currentPage;
		this.totalCount = totalCount;
	}

	public int start() {
		if (pageNo > 0) {
			return (pageNo - 1) * pageSize;
		}
		return 0;
	}

}
