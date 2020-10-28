package it.course.myblog.service;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import it.course.myblog.entity.DBFile;
import it.course.myblog.repository.DBFileRepository;

@Service
public class DBFileService {

    @Autowired
    DBFileRepository dbFileRepository;
    
    @Value("${post.image.width}")
    int width;
    
    @Value("${post.image.heigth}")
    int heigth;
    
    @Value("${user.image.width}")
    int widthAvatar;
    
    @Value("${user.image.heigth}")
    int heigthAvatar;
    
    @Value("${post.image.size}")
    int sizeImagePost;
    
    public String ctrlImageLength(MultipartFile dbFile) {

    	long octets = dbFile.getSize();

    	if(octets > sizeImagePost)
    		return "The image size must be less than sizeImagePost: " + sizeImagePost/1024 + " Kb";

    	return null;
    }
    

    public DBFile fromMultiToDBFile(MultipartFile file) {

    	String fileName = StringUtils.cleanPath(file.getOriginalFilename());

    	DBFile dbFile = null;
		try {
			dbFile = new DBFile(fileName, file.getContentType(), file.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}

    	return dbFile;
    }
    
    public BufferedImage getBufferedImage(MultipartFile dbFile) {
    	
    	BufferedImage image = null;
		try {
			image = ImageIO.read(dbFile.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
		
    }
    
    public String ctrlImageSize(BufferedImage bf) { 
		
		if(bf != null) {
			if(bf.getWidth() > width || bf.getHeight() > heigth ) {
				return "The image size must be "+width+"x"+heigth;
			}
		}
		return null;
    }
    
    public String ctrlImageSizeAvatar(BufferedImage bf) { 
		
		if(bf != null) {
			if(bf.getWidth() > widthAvatar || bf.getHeight() > heigthAvatar ) {
				return "The image size must be "+widthAvatar+"x"+heigthAvatar;
			}
		}
		return null;
    }
    
    public DBFile copyImageContent(DBFile oldDBFile, DBFile newDBFile) {

    	oldDBFile.setFileName(newDBFile.getFileName());
    	oldDBFile.setFileType(newDBFile.getFileType());
    	oldDBFile.setData(newDBFile.getData());

    	return oldDBFile;
    }
    
}