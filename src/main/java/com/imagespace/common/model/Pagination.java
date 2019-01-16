package com.imagespace.common.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Pagination {

	/**
	 * 当前页数
	 */
	private int pageNo;
	/**
	 * 每页展示
	 */
	private int pageSize;
	private int totalCount;

	public Pagination(int pageNo, int pageSize) {
		this.pageNo = pageNo;
		this.pageSize = pageSize;
	}

	public int start() {
		return pageNo > 0 ? (pageNo - 1) * pageSize : 0;
	}

}
