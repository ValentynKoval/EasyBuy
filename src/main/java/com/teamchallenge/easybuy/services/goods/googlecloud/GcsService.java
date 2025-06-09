//package com.teamchallenge.easybuy.services.goods.googlecloud;
//
//import jakarta.annotation.PostConstruct;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.UUID;
//
//@Service
//public class GcsService {
//
//    @Value("${google.cloud.gcs.bucket-name}")
//    private String bucketName;
//
//    @Value("${google.cloud.gcs.credentials-location}")
//    private String credentialsLocation;
//
//    private Storage storage;
//
//    @PostConstruct // Аннотация, которая указывает Spring выполнить этот метод после инициализации бина
//    public void init() throws IOException {
//        // Логика инициализации Storage.
//        // Определяем, откуда брать учетные данные: из classpath или по абсолютному пути.
//        if (credentialsLocation.startsWith("classpath:")) {
//            String resourcePath = credentialsLocation.replace("classpath:", "");
//            try (InputStream serviceAccountStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
//                if (serviceAccountStream == null) {
//                    throw new IOException("Service account key file not found in classpath: " + credentialsLocation);
//                }
//                this.storage = StorageOptions.newBuilder()
//                        .setCredentials(com.google.auth.oauth2.GoogleCredentials.fromStream(serviceAccountStream))
//                        .build()
//                        .getService();
//            }
//        } else {
//            // Предполагаем, что это абсолютный путь к файлу
//            try (InputStream serviceAccountStream = Files.newInputStream(Paths.get(credentialsLocation))) { // Убедитесь, что Files импортирован
//                this.storage = StorageOptions.newBuilder()
//                        .setCredentials(com.google.auth.oauth2.GoogleCredentials.fromStream(serviceAccountStream))
//                        .build()
//                        .getService();
//            }
//        }
//    }
//
//    /**
//     * Загружает файл в Google Cloud Storage.
//     * @param file Объект MultipartFile, представляющий загружаемый файл.
//     * @return Публичная URL-ссылка на загруженный файл.
//     * @throws IOException Если произошла ошибка при загрузке.
//     */
//    public String uploadFile(MultipartFile file) throws IOException {
//        String originalFilename = file.getOriginalFilename();
//        // Генерируем уникальное имя файла, чтобы избежать коллизий
//        // Рекомендуется также добавить что-то, что идентифицирует товар, но UUID достаточно для уникальности
//        String fileName = UUID.randomUUID().toString() + "_" + originalFilename;
//
//        BlobId blobId = BlobId.of(bucketName, fileName);
//        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
//                .setContentType(file.getContentType())
//                .build();
//
//        Blob blob = storage.create(blobInfo, file.getBytes());
//
//        // Возвращаем публичную URL-ссылку на объект
//        // ВНИМАНИЕ: Это будет работать только если ваш бакет или конкретные объекты публично доступны для чтения.
//        // Для продакшена часто используется Signed URL (подписанные URL), которые предоставляют временный доступ.
//        return blob.getMediaLink();
//    }
//
//    /**
//     * Удаляет файл из Google Cloud Storage.
//     * @param fileUrl URL-ссылка на файл, который нужно удалить (должен быть публичной ссылкой от getMediaLink()).
//     * @return true, если файл успешно удален, false в противном случае.
//     */
//    public boolean deleteFile(String fileUrl) {
//        String fileName = extractFileNameFromUrl(fileUrl);
//        if (fileName == null) {
//            System.err.println("Could not extract file name from URL: " + fileUrl);
//            return false;
//        }
//        BlobId blobId = BlobId.of(bucketName, fileName);
//        return storage.delete(blobId);
//    }
//
//    // Вспомогательный метод для извлечения имени файла из URL
//    private String extractFileNameFromUrl(String fileUrl) {
//        if (fileUrl == null || fileUrl.isEmpty()) {
//            return null;
//        }
//        // GCS MediaLink URL обычно выглядит так:
//        // https://storage.googleapis.com/your-bucket-name/unique_filename.jpg?X-Goog-resumable=...
//        // Или просто: https://storage.googleapis.com/your-bucket-name/unique_filename.jpg
//
//        // Находим индекс последнего слеша
//        int lastSlashIndex = fileUrl.lastIndexOf('/');
//        if (lastSlashIndex == -1) {
//            return null; // Нет слешей, не похоже на URL GCS
//        }
//
//        String filenameWithQueryParams = fileUrl.substring(lastSlashIndex + 1);
//
//        // Удаляем любые параметры запроса, если они есть
//        int queryParamIndex = filenameWithQueryParams.indexOf('?');
//        if (queryParamIndex != -1) {
//            return filenameWithQueryParams.substring(0, queryParamIndex);
//        }
//
//        return filenameWithQueryParams;
//    }
//}