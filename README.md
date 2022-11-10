# Movie Database 🎥

Это лаба по [ПРБП](https://edufpmi.bsu.by/course/view.php?id=643). К приложению предъявлялись требования:
- субд
- веб-интерфейс
- возможность создавать/редактировать/удалять записи
- one-to-many, many-to-many связи между данными

![image](https://user-images.githubusercontent.com/41614960/143312945-5098d0e5-4213-4ba0-b1c7-c03095e495f3.png)

![image](https://user-images.githubusercontent.com/41614960/143313067-dbd7f825-9d6e-4247-9093-bc1d4cbf7e60.png)

## Как собрать и запустить 🚀

Для запуска проекта нужна только JDK актуальной версии

```bash
./mvnw clean package -Pproduction
# mvnw clean package -Pproduction на windows
java -jar target/moviedatabase-1.0-SNAPSHOT.jar
```

Приложение будет доступно по ссылке http://localhost:8080/
