package com.invenia.excel.converter.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Item {

  private String idxNo; // 순번
  private String itemName; // 품명
  private String itemNo; // 품번
  private String spec; // 규격
  private String assetName; // 품목 자산 분류
  private String itemClassSName; // 사이트 분류 -> 품목 소분류
  private String smInOutKindName; // 내외자 구분
  private String orderDate; // 주문일 Date
  private String custName; // 거래처
  private String deliveryDate; // 배송일
  private String dvPlaceName; // 공급사
  private String remarkM; // 특이사항
  private String unitName; // 단위
  private String qty; // 수량
  private String price; // 단가
  private String optionAmt; // 옵션가
  private String smVatKindName; // 부가세구분
  private String curAmt; // 판매금액
  private String purAmt; // 매입금액
  private String whName; // 창고

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Item item = (Item) o;

    return itemNo.equals(item.itemNo);
  }

  @Override
  public int hashCode() {
    return itemNo.hashCode();
  }
}
