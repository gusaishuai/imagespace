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
	private int pageSize = 15;
	private int totalCount;

	public Pagination(int currentPage, int totalCount) {
		this.pageNo = currentPage < 1 ? 1 : currentPage;
		this.totalCount = totalCount;
	}

	public int start() {
		if (pageNo > 0) {
			int start = (pageNo - 1) * pageSize;
			return start > totalCount ? totalCount : start;
		}
		return 0;
	}

	public int end() {
		if (pageNo > 0) {
			int end = pageNo * pageSize;
			return end > totalCount ? totalCount : end;
		}
		return 0;
	}

}
