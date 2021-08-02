package com.invenia.excel.converter.dto;

import lombok.Data;

@Data
public class ConvertResult {
  private int customerSize;
  private int itemCodeSize;
  private long itemPriceSize;
  private int contractOrderSize;
}
