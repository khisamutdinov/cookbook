package net.shamansoft.cookbook.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;

@Service
@Slf4j
public class CompressorHTMLBase64 implements Compressor {

    @Override
    public String decompress(String content) throws IOException {
        log.debug("Decompressing content of size: {}", content.length());

        try {
            // Decode Base64
            byte[] compressedBytes = Base64.getDecoder().decode(content);

            // Decompress with GZIP
            try (ByteArrayInputStream byteStream = new ByteArrayInputStream(compressedBytes);
                 GZIPInputStream gzipStream = new GZIPInputStream(byteStream);
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[1024];
                int len;
                while ((len = gzipStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, len);
                }

                // Convert to string
                String result = outputStream.toString(StandardCharsets.UTF_8);
                log.debug("Decompressed to size: {}", result.length());
                return result;
            }
        } catch (IllegalArgumentException e) {
            log.warn("Content doesn't appear to be Base64 encoded: {}", e.getMessage());
            throw new IOException("Content isn't Base64 encoded", e);
        }
    }
}
