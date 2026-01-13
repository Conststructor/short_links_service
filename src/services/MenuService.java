package services;

import exceptions.MenuException;
import models.Link;
import models.User;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class MenuService {
    Scanner scanner = new Scanner(System.in);
    LinkShorterService LSS = new LinkShorterService();
    User user = new User();
    Link link;

    public void showMainMenu() {
        System.out.println("0 - выйти");
        System.out.println("1 - создать новую ссылку");
        System.out.println("2 - перейти по короткой ссылке");
        System.out.println("3 - изменить параметры короткой ссылки");
    }

    public void handleMainMenu() {
        boolean mark = true;
        String input;
        System.out.println("Запуск приложения сокращения ссылок !");

        do {
            showMainMenu();

            do {
                input = scanner.nextLine().trim();
            } while (input.isEmpty());

            switch (input) {
                case "0":
                    mark = false;
                    System.out.println("Программа завершена.");
                    break;
                case "1":
                    createNewLinkMenu();
                    break;
                case "2":
                    goToLinkMenu();
                    break;
                case "3":
                    changeShortLinkMenu();
                    break;
            }
        } while (mark);
    }

    public void showChangeShortLinkMenu() {
        System.out.println("0 - назад");
        System.out.println("1 - изменить количество переходов по ссылке");
        System.out.println("2 - изменить время действия ссылки");
        System.out.println("3 - удалить ссылку");
    }

    public void createNewLinkMenu() {
        String url;
        int numForward;
        Duration duration;

        // создание обычной ссылки
        do {
            System.out.println("Введите ссылку : ");
            url = scanner.nextLine().trim();
        } while (!LSS.isValidLink(url));

        // кол-во переходов по ссылке
        System.out.println("Введите количество переходов по ссылке : ");
        try {
            numForward = scanner.nextInt();
        } catch (MenuException e) {
            throw new MenuException("Введено неправильное значение" + e.getMessage());
        }

        // срок действия ссылки
        duration = LSS.linkDurationInput();

        link = new Link(url, user, numForward, duration);
        LSS.createShortLink(link);
        System.out.println("Ссылка успешно создана : " + link.getShortUrl());
    }

    public void changeShortLinkMenu() {
        showChangeShortLinkMenu();
        String input = scanner.nextLine().trim();

        switch (input) {
            case "0":
                handleMainMenu();
                break;
            case "1":
                changeLinkForwardHandler();
                break;
            case "2":
                changeLinkLifeTimeHandler();
                break;
            case "3":
                deleteLinkHandler();
                break;
        }
    }

    public void changeLinkForwardHandler() {
        System.out.println("Доступные ссылки : ");
        LSS.displayShortLinkList();
        System.out.println("Введите ссылку для изменения : ");
        String str = scanner.nextLine().trim();

        System.out.println("Оставшееся количество переходов : " + LSS.getLinkFromList(str).getLinkForwardLimit());
        System.out.println("Введите новое количество переходов : ");
        int num = scanner.nextInt();
        if (num > 0) {
            LSS.changeLinkForwardNumber(str, num);
            System.out.println("ОБНОВЛЁННЫЕ ДАННЫЕ ПОСЛЕ ИЗМЕНЕНИЯ");
            System.out.println("Установленное количество переходов : " + LSS.getLinkFromList(str).getLinkForwardLimit());
        }
        else
            System.out.println("Количество переходов должно быть положительным");
    }

    public void changeLinkLifeTimeHandler() {
        System.out.println("Введите ссылку для изменения : ");
        String str = scanner.nextLine().trim();

        DateTimeFormatter formatPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println("Время создания : " + LSS.getLinkFromList(str).getCreateTime().format(formatPattern) +
                " | Время действия : " + LSS.getLinkFromList(str).getLifeTime() +
                " | Время окончания действия ссылки : " + LSS.getLinkFromList(str).getExpireTime().format(formatPattern));

        Duration duration = LSS.linkDurationInput();
        LSS.changeLinkLifeTime(str, duration);

        System.out.println("ОБНОВЛЁННЫЕ ДАННЫЕ ПОСЛЕ ИЗМЕНЕНИЯ");
        System.out.println("Время создания : " + LSS.getLinkFromList(str).getCreateTime() +
                "Время действия : " + LSS.getLinkFromList(str).getLifeTime() +
                "Время окончания действия ссылки : " + LSS.getLinkFromList(str).getExpireTime());
    }

    public void deleteLinkHandler() {
        System.out.println("Доступные ссылки : ");
        LSS.displayShortLinkList();
        System.out.println("Введите короткую ссылку для удаления");
        String str = scanner.nextLine().trim();
        LSS.deleteShortLinkFromMap(str);
    }

    public void goToLinkMenu() {
        System.out.println("Доступные ссылки : ");
        LSS.displayShortLinkList();
        System.out.println("Вставьте короткую ссылку : ");
        String input = scanner.nextLine().trim();
        try {
            LSS.goToShortLink(input);
        } catch (Exception e) {
            throw new MenuException("Ошибка, такой ссылки не найдено : " + e.getMessage());
        }
    }

}
