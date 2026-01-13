package services;

//public class services.Link_Shorter {
//    private Map<String, String> linkMap = new HashMap<>();
//    private Map<String, String> userLinkMap = new HashMap<>();
//
//    public String services.Link_Shorter(String originalLink, String userId) {
//        // Генерируем уникальный код для ссылки
//        String shortCode = UUID.randomUUID().toString().substring(0, 6);
//
//        // Проверяем, существует ли уже такая ссылка для пользователя
//        if (userLinkMap.containsKey(userId)) {
//            // Если да, то добавляем к существующей ссылке новый код
//            String existingShortCode = userLinkMap.get(userId);
//            shortCode = existingShortCode + "_" + shortCode;
//        }
//
//        // Сохраняем связь между исходной ссылкой и сокращенной ссылкой
//        linkMap.put(shortCode, originalLink);
//
//        // Сохраняем связь между пользователем и сокращенной ссылкой
//        userLinkMap.put(userId, shortCode);
//
//        return "http://short.url/" + shortCode;
//    }
//
//    public String getOriginalLink(String shortCode) {
//        return linkMap.get(shortCode);
//    }
//
//    public String getShortLinkForUser(String userId) {
//        return userLinkMap.get(userId);
//    }
//}

import exceptions.LinkServiceException;
import models.Link;
import models.User;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LinkShorterService {
    private static final String CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = CHARSET.length(); // 62
    private static Map<String, Link> shortLinkList = new HashMap<>();

    public String encode(long num) {
        if (num == 0) return "0";

        StringBuilder sb = new StringBuilder();

        while (num > 0) {
            int index = (int) (num % BASE);
            sb.append(CHARSET.charAt(index));
            num /= BASE;
        }

        return sb.reverse().toString();
    }

    public long decode(String str) {
        long result = 0;

        for (int i = 0; i < str.length(); i++) {
            result = result * BASE + CHARSET.indexOf(str.charAt(i));
        }

        return result;
    }

    public Duration linkDurationInput() {
        System.out.println("Задайте длительность действия ссылки (формат: чч:мм:сс) : ");

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        try {
            Duration duration = parseDuration(input);
            System.out.println("Заданная длительность: " + duration.toSeconds() / 3600 + "ч " +
                    duration.toSeconds() % 3600 / 60 + "м " + duration.toSeconds() % 60 + "c");
            return duration;
        } catch (Exception e) {
            System.out.println("Ошибка ввода: " + e.getMessage());
        }
        return null;
    }

    // Парсинг пользовательского ввода
    private Duration parseDuration(String input) {
        String[] parts = input.split(":");

        if (parts.length != 3) {
            throw new IllegalArgumentException("Неверный формат. Используйте чч:мм:сс");
        }

        long hours = Long.parseLong(parts[0]);
        long minutes = Long.parseLong(parts[1]);
        long seconds = Long.parseLong(parts[2]);

        return Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds);
    }

    // создание уникальной короткой ссылки
    public void createShortLink(Link link) {
        if (isValidLink(link.getUrl())) {
            String shortLink = encode(link.getId());
            link.setShortUrl(shortLink);
            shortLinkList.put(shortLink, link);
        } else
            throw new LinkServiceException("Ошибка при создании короткой ссылки");
    }

    // добавление ссылки в список
    public void addShortLinkToMap(Link link) {
        String shortLink = encode(link.getUrl().length());
        link.setShortUrl(shortLink);
        shortLinkList.put(shortLink, link);
    }

    //вывод всех доступных коротких ссылок
    public void displayShortLinkList() {
        for (String key : shortLinkList.keySet()) {
            System.out.println(key);
        }
    }

    // вывод короткой ссылки из списка
    public Link getLinkFromList(String shortLink) {
        try {
            return shortLinkList.get(shortLink);
        } catch (Exception e) {
            throw new RuntimeException("Неверно обработана ссылка " + e.getMessage());
        }
    }

    // изменение количества переходов
    public void changeLinkForwardNumber(String str, int num) {
        shortLinkList.get(str).setLinkForwardLimit(num);


    }

    //изменение времени функционирования
    public void changeLinkLifeTime(String str, Duration duration) {
        System.out.println("Оставшееся время действия ссылки : " + shortLinkList.get(str).getExpireTime());
        shortLinkList.get(str).setLifeTime(duration);
    }

    // удаление ссылки из списка
    public void deleteShortLinkFromMap(String shortLink) {
        try {
            shortLinkList.remove(shortLink);
        } catch (Exception e) {
            throw new RuntimeException("Неверно обработана ссылка на удаление " + e.getMessage());
        }
    }

    // переход по ссылке
    public void goToShortLink(String inputStr) {
        // проверки на валидность ссылки
        if (isExpired(shortLinkList.get(inputStr))) {
            System.out.println("Время действия ссылки истекло, ссылка удалена.");
            shortLinkList.remove(inputStr);
        } else {
            try {
                Desktop.getDesktop().browse(new URI(shortLinkList.get(inputStr).getUrl()));
                shortLinkList.get(inputStr).setLinkForwardLimit(shortLinkList.get(inputStr).getLinkForwardLimit() - 1); //уменьшаем лимит переходов по ссылке на 1
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // парсинг ссылки
    public boolean isValidLink(String str) {
        if (str.length() == 0) return false;
        String regex = "^(http|https)://.*\\.[a-z]{2,6}(/.*)?";
        return str.matches(regex);
    }

    // проверка ссылки на валидность
    public static boolean isExpired(Link link) {
        if (LocalDateTime.now().isBefore(link.getExpireTime()) & link.getLinkForwardLimit() > 0)
            return false;
        else
            return true;
    }
}