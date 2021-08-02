package com.invenia.excel.converter.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Price extends ItemCode {

  private String price;
}
