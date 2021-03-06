package com.invenia.excel.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExcelFileUtils {

  public void fileBackup(Path backupPath, Path downloadPath, Path outputPath) throws IOException {
    log.info("파일 백업 시작");
    try {
      // make backup folder
      if (!Files.exists(backupPath)) {
        Files.createDirectories(backupPath);
      }
      // Chrome Source Backup
      if (Files.exists(downloadPath)) {
        fileMove(downloadPath, backupPath);
      }
      // Output Backup
      if (Files.exists(outputPath)) {
        fileCopy(outputPath, backupPath);
      }
      String zipPath = backupPath.getParent().resolve(backupPath.getFileName() + ".zip").toString();
      new ZipFile(zipPath).addFolder(backupPath.toFile());
      deleteDirectory(backupPath);
    } catch (IOException e) {
      log.error(e.getLocalizedMessage(), e);
      throw e;
    }
    log.info("파일 백업 완료");
  }

  public void fileMove(Path source, Path target) throws IOException {
    if (!Files.exists(source)) {
      Files.createDirectories(source);
    }
    Files.walk(source, 1)
        .filter(Files::isRegularFile)
        .filter(
            path -> path.getFileSystem().getPathMatcher("glob:**.{xls,xlsx,html,png}").matches(path))
        .forEach(
            p -> {
              try {
                Files.move(p, target.resolve(p.getFileName()), StandardCopyOption.REPLACE_EXISTING);
              } catch (IOException e) {
                log.error(e.getLocalizedMessage(), e);
              }
            });
  }

  public void fileCopy(Path source, Path target) throws IOException {
    if (Files.isDirectory(source)) {
      if (Files.notExists(target)) {
        Files.createDirectories(target);
      }
      try (Stream<Path> paths = Files.list(source)) {
        paths.forEach(
            p -> {
              try {
                fileCopy(p, target.resolve(source.relativize(p)));
              } catch (IOException e) {
                log.error(e.getLocalizedMessage(), e);
              }
            });
      }
    } else {
      // if file exists, replace it
      Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
      log.debug(String.format("Copy File from \t'%s' to \t'%s'", source, target));
    }
  }

  public void deleteDirectory(Path path) throws IOException {
    if (Files.exists(path)) {
      try (Stream<Path> walk = Files.walk(path)) {
        walk.sorted(Comparator.reverseOrder())
            .forEach(
                p -> {
                  try {
                    log.info("{} 삭제", p);
                    Files.delete(p);
                  } catch (IOException e) {
                    log.error(e.getLocalizedMessage(), e);
                  }
                });
      } catch (IOException e) {
        log.error(e.getLocalizedMessage(), e);
        throw e;
      }
    }
  }

  public void deleteSubDirectory(Path path) throws IOException {
    if (Files.exists(path)) {
      try (Stream<Path> walk = Files.walk(path)) {
        walk.sorted(Comparator.reverseOrder())
            .filter(p -> !p.equals(path))
            .forEach(
                p -> {
                  try {
                    log.info("{} 삭제", p);
                    Files.delete(p);
                  } catch (IOException e) {
                    log.error(e.getLocalizedMessage(), e);
                  }
                });
      } catch (IOException e) {
        log.error(e.getLocalizedMessage(), e);
        throw e;
      }
    }
  }
}
