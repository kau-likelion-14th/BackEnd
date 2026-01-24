package likelion14th.lte.utils.Image;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Component
public class ImageUtil {

    private static final Tika tika=new Tika();
    public record ResizedImage(byte[] bytes, String contentType, String extension) {}

    private void validateImage(MultipartFile file) throws IOException {
        String detectedType = tika.detect(file.getInputStream());
        Set<String> allowed = Set.of("image/png", "image/jpg", "image/jpeg","image/webp");

        if(!allowed.contains(detectedType)){
            throw new IllegalArgumentException("이미지 파일이 아닙니다"+detectedType);
        }

        long maxSize = 50*1024*1024L;

        if(file.getSize() <=0 || file.getSize()>maxSize){
            throw new IllegalArgumentException("파일이 너무 큽니다"+file.getSize());
        }
    }

    public ResizedImage resizeProfileToWebpBytes(MultipartFile file, int size) throws IOException {

        BufferedImage output;
        try (InputStream is = file.getInputStream()) {
            output = Thumbnails.of(is)
                    .size(size, size)
                    .crop(Positions.CENTER)   // 비율 유지 + 크면 축소 + 작으면 확대 + 중앙 크롭
                    .asBufferedImage();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(output, "webp", baos);   // WebP 출력

        return new ResizedImage(
                baos.toByteArray(),
                "image/webp",
                "webp"
        );
    }

}
