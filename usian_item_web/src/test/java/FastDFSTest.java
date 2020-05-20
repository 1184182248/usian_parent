import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.domain.ThumbImageConfig;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.usian.ItemWebApp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes={ItemWebApp.class})
public class FastDFSTest {

    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private ThumbImageConfig thumbImageConfig;

    @Test
    public void testUpload() throws FileNotFoundException {

        File file = new File("D:/images/2.jpg");

        StorePath storePath = this.storageClient.uploadFile(
                new FileInputStream(file), file.length(), "jpg", null);

        System.out.println(storePath.getFullPath());

        System.out.println(storePath.getPath());
    }

    @Test
    public void testUploadAndCreateThumb() throws FileNotFoundException {
        File file = new File("D:/images/2.jpg");

        StorePath storePath = this.storageClient.uploadImageAndCrtThumbImage(
                new FileInputStream(file), file.length(), "png", null);

        System.out.println(storePath.getFullPath());

        System.out.println(storePath.getPath());

        String path = thumbImageConfig.getThumbImagePath(storePath.getFullPath());
        System.out.println(path);
    }
}
