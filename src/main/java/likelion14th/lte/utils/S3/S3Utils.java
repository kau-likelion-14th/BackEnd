package likelion14th.lte.utils.S3;

import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import likelion14th.lte.global.config.AmazonConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;



import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Utils {
    //TODO 커스텀해야겠지?

    private final S3Client s3Client;
    private final AmazonConfig config;


    //클래스 내부 유틸 메서드
    private String generateFileKey(String fileName) {

        return UUID.randomUUID() + "-" + fileName;
    }

    private String getUrl(String key){
        return "https://"+config.getBucket()+".s3"+config.getRegion()+".amazonaws.com/"+key;
    }

    //공용 메서드

    public S3Dto uploadFile(MultipartFile file) throws IOException {
        if (file.getOriginalFilename() == null) {
            throw new IllegalArgumentException("File name cannot be null");
        }
        String fileKey = generateFileKey(file.getName());
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(config.getBucket())
                .key(fileKey)
                .contentType(file.getContentType())
                .build();

        try(InputStream inputStream = file.getInputStream()){
            s3Client.putObject(req, RequestBody.fromInputStream(inputStream, file.getSize()));
        }

        return new S3Dto(getUrl(fileKey),fileKey);
    }


    public S3Dto uploadBytes(byte[] bytes, String fileName, String contentType) {
        String key = generateFileKey(fileName);
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(config.getBucket())
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(req, RequestBody.fromBytes(bytes));

        return new S3Dto(key, getUrl(key));
    }

    public void deleteFile(String fileKey) {
        try {
            DeleteObjectRequest req = DeleteObjectRequest.builder()
                    .bucket(config.getBucket())
                    .key(fileKey)
                    .build();
            s3Client.deleteObject(req);
            log.info("Deleted file from S3: {}", fileKey);
        } catch (SdkClientException e) {
            log.error("Error occurred while deleting file from S3: {}", fileKey, e);
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }


}