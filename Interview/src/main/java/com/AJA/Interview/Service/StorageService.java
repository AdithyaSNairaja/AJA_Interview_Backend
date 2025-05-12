package com.AJA.Interview.Service;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;

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
		return "File uploaded "+fileName;
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
}
