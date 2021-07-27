package com.invenia.excel.web.controller;

import com.invenia.excel.batch.BatchJob;
import com.invenia.excel.batch.config.BatchConfig;
import com.invenia.excel.converter.ConvertConfig;
import com.invenia.excel.web.dto.ManualRunSettings;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class BatchRunController {

  private final ConvertConfig config;
  private final BatchJob batchJob;
  private final JobLauncher jobLauncher;

  @PostMapping(value = "/run/all")
  public ResponseEntity<String> runAll(@RequestBody ManualRunSettings runSettings)
      throws Exception {
    jobLauncher.run(batchJob.allProcessJob(),
        BatchConfig.getJobParameters(runSettings.getFromDate(), runSettings.getToDate()));
    return ResponseEntity.ok().build();
  }

  @PostMapping(value = "/run/item")
  public ResponseEntity<String> runItem(@RequestBody ManualRunSettings runSettings)
      throws Exception {
    jobLauncher.run(batchJob.itemCodeJob(),
        BatchConfig.getJobParameters(runSettings.getFromDate(), runSettings.getToDate()));
    return ResponseEntity.ok().build();
  }

  @PostMapping(value = "/run/customer")
  public ResponseEntity<String> runCustomer(@RequestBody ManualRunSettings runSettings)
      throws Exception {
    jobLauncher.run(batchJob.customerCheckJob(),
        BatchConfig.getJobParameters(runSettings.getFromDate(), runSettings.getToDate()));
    return ResponseEntity.ok().build();
  }

  @PostMapping(value = "/run/contract")
  public ResponseEntity<String> runContract(@RequestBody ManualRunSettings runSettings)
      throws Exception {
    jobLauncher.run(batchJob.contractOrderJob(),
        BatchConfig.getJobParameters(runSettings.getFromDate(), runSettings.getToDate()));
    return ResponseEntity.ok().build();
  }

  @PostMapping(value = "/run/purchase")
  public ResponseEntity<String> runPurchase(@RequestBody ManualRunSettings runSettings)
      throws Exception {
    jobLauncher.run(batchJob.purchaseOrderJob(),
        BatchConfig.getJobParameters(runSettings.getFromDate(), runSettings.getToDate()));
    return ResponseEntity.ok().build();
  }

  @GetMapping(value = "/purchase-order/{jobExecutionId}")
  public ResponseEntity<Resource> downloadPurchaseOrderFile(
      @PathVariable String jobExecutionId, HttpServletResponse response) throws IOException {
    Path backupPath = Paths.get(config.getBackupPath()).resolve(jobExecutionId);
    Path zipFilePath = backupPath.resolve("purchaseorder.zip");
    makeFilteredZipFile(backupPath, zipFilePath, "purchaseorder.xlsx");
    ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(zipFilePath));
    return ResponseEntity.ok()
        .headers(prepareHeaderForFileReturn(zipFilePath.getFileName().toString(),
            "application/zip", response))
        .contentLength(Files.size(zipFilePath))
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(resource);
  }

  @GetMapping(value = "/unregistered-customer/{jobExecutionId}")
  public ResponseEntity<Resource> downloadUnregisteredCustomerFile(
      @PathVariable String jobExecutionId, HttpServletResponse response) throws IOException {
    Path backupPath = Paths.get(config.getBackupPath())
        .resolve(jobExecutionId)
        .resolve("customer.xlsx");
    ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(backupPath));
    return ResponseEntity.ok()
        .headers(prepareHeaderForFileReturn(backupPath.getFileName().toString(),
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", response))
        .contentLength(Files.size(backupPath))
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(resource);
  }

  private HttpHeaders prepareHeaderForFileReturn(String fileName, String contentType,
      HttpServletResponse response) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.CONTENT_TYPE, contentType);
    response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
    return headers;
  }

  private void makeFilteredZipFile(Path source, Path zipFile, String filterFileName) {
    try (ZipOutputStream outputStream = new ZipOutputStream(
        new FileOutputStream(zipFile.toString()))) {
      Files.walkFileTree(source, new SimpleFileVisitor<>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
          if (!file.getFileName().toString().equals(filterFileName)) {
            return FileVisitResult.CONTINUE;
          }

          try {
            Path targetFile = source.relativize(file);
            outputStream.putNextEntry(new ZipEntry(targetFile.toString()));
            byte[] bytes = Files.readAllBytes(file);
            outputStream.write(bytes, 0, bytes.length);
            outputStream.closeEntry();
          } catch (IOException e) {
            e.printStackTrace();
          }
          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException e) {
      log.error(e.getLocalizedMessage(), e);
    }
  }
}
