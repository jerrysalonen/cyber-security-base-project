/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projekti;

import java.io.IOException;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author salon
 */
public class ImageService {
    @Autowired
    private ImageRepository imgRepo;

    public Image storeFile(MultipartFile file, Account user) throws Exception {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if(fileName.contains("..")) {
                throw new Exception("Invalid path name: " + fileName);
            }

            Image img = new Image();
            img.setUser(user);
            img.setFileName(fileName);
            img.setFileType(file.getContentType());
            img.setImage(file.getBytes());
            img.setPostedAt(new Date());

            return imgRepo.save(img);
        } catch (IOException ex) {
            throw new Exception("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Image getFile(Long fileId) throws Exception {
        return imgRepo.findById(fileId)
                .orElseThrow(() -> new Exception("File not found with id " + fileId));
    }
    
    @Transactional
    public Image setAsProfilePic(Long fileId) {
        Image img = imgRepo.getOne(fileId);
        Image currProfPic = imgRepo.findByProfilePic(true);
        
        if (currProfPic != null) {
            currProfPic.setProfilePic(false);
        }
        
        img.setProfilePic(true);
        
        imgRepo.save(currProfPic);
        return imgRepo.save(img);
    }
}
