package com.agoracorp.projectx.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryService {

	private final Cloudinary cloudinary;

	public CloudinaryService(Cloudinary cloudinary) {
		this.cloudinary = cloudinary;
	}

	public String uploadAvatar(MultipartFile file, Long userId) throws IOException {
		Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
			"folder", "projectx/avatars",
			"public_id", "user_" + userId,
			"overwrite", true
		));
		return (String) result.get("secure_url");
	}

	public String uploadCover(MultipartFile file, Long userId) throws IOException {
		Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
			"folder", "projectx/covers",
			"public_id", "user_" + userId,
			"overwrite", true
		));
		return (String) result.get("secure_url");
	}
}
