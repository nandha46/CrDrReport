package in.trident.crdr.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

import org.springframework.web.multipart.MultipartFile;

public class MD5Checksum {

	public static String checksum(MessageDigest digest, MultipartFile file) throws IOException {

		InputStream IStream = file.getInputStream();
		byte[] byteArray = new byte[1024];
		int bytesCount = 0;
		while ((bytesCount = IStream.read(byteArray)) != -1) {
			digest.update(byteArray, 0, bytesCount);
		}
		IStream.close();
		byte[]	bytes = digest.digest();
		StringBuilder sb = new StringBuilder();
		for (int i =0; i < bytes.length;i++) {
			sb.append(Integer
                    .toString((bytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
		}
		return sb.toString();
	}
}
