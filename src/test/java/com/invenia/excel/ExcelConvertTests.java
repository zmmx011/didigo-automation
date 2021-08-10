package com.invenia.excel;

import com.invenia.excel.converter.ExcelConverter;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dev")
@SpringBootTest
class ExcelConvertTests {

  @Autowired
  private ExcelConverter excelConverter;

  @Test
  void makeItemExcel() throws Exception {
    System.setProperty("java.awt.headless", "false");
    excelConverter.init();
    excelConverter.itemConvert("mall");
    // excelConverter.unregisteredCustomerCheck("mall");
  }

  @Test
  void clipboardCopy() throws Exception {
    System.setProperty("java.awt.headless", "false");
    StringSelection data =
        new StringSelection(
            "[르크루제] 원형 무쇠 냄비 22cm [색상 : 카시스]\tL000566248\t(주)까사벨라\t225,000.00\t내수\t2021-07-30\n");
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(data, data);
  }
}
