package com.AJA.Interview.Service;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StorageService {

	@Value("${aws.s3.bucket-name}")
	private String bucketName;
	
	@Autowired
	private AmazonS3 amazonS3; 
	
	public String uploadFile(MultipartFile file) {
		File fileObj=convertMultiPartFile(file);
		String fileName=System.currentTimeMillis()+"_"+file.getOriginalFilename();
		amazonS3.putObject(new PutObjectRequest(bucketName,fileName,fileObj));
		String fileUrl = amazonS3.getUrl(bucketName, fileName).toString();
		fileObj.delete();
		return fileName;
	}
	
	private File convertMultiPartFile(MultipartFile file) {
		File convertedFile = new File(file.getOriginalFilename());
		try(FileOutputStream fos=new FileOutputStream(convertedFile)){
			fos.write(file.getBytes());
		}catch(IOException e) {
			log.error("");
		}
		return convertedFile;
	}
	
	public void deleteFile(String fileUrl) {
	    String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
	    amazonS3.deleteObject(bucketName, fileName);
	}
	
	public ResponseEntity<?> downloadFile(String fileKey) {
        try {
            // If a full URL is passed, extract just the file name (S3 key)
            String s3Key = fileKey.contains("/") ? fileKey.substring(fileKey.lastIndexOf("/") + 1) : fileKey;
            log.info("Downloading S3 file with key: {}", s3Key);
            S3Object s3Object = amazonS3.getObject(bucketName, s3Key);
            InputStreamResource resource = new InputStreamResource(s3Object.getObjectContent());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + s3Key + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (AmazonServiceException e) {
            return ResponseEntity.status(500).body("Error downloading file from S3: " + e.getMessage());
        }
    }

}
