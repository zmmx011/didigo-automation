package com.invenia.excel.converter.dto;

import lombok.Data;

@Data
public class ItemCode {
	private String itemCode;

	@Override
	public boolean equals(Object o) {
		return itemCode.equals(o.toString());
	}

	@Override
	public int hashCode() {
		return itemCode != null ? itemCode.hashCode() : 0;
	}
}
