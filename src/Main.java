import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

class Account {
    private AtomicInteger balance = new AtomicInteger(0); // Потокобезопасный баланс

    // Метод для пополнения счета на определенную сумму
    public void deposit(int amount) {
        balance.addAndGet(amount);
        System.out.println("Пополнено на: " + amount + ". Текущий баланс: " + balance.get());
    }

    // Метод для снятия денег, если хватает баланса
    public void withdraw(int amount) {
        while (balance.get() < amount) {
            System.out.println("Недостаточно средств. Ожидание пополнения...");
            try {
                Thread.sleep(500); // Ждем пополнения
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Поток прерван.");
                return;
            }
        }
        balance.addAndGet(-amount);
        System.out.println("Снято: " + amount + ". Текущий баланс: " + balance.get());
    }

    // Метод для получения текущего баланса
    public int getBalance() {
        return balance.get();
    }
}

public class Main {
    public static void main(String[] args) {
        Account account = new Account();
        Thread depositThread = getThread(account);

        // Запускаем поток пополнения
        depositThread.start();

        // Метод снятия денег
        new Thread(() -> account.withdraw(4000)).start(); // Пытаемся снять 4000

        // Ожидаем завершения потоковgit
        try {
            depositThread.join();
        } catch (InterruptedException e) {
            System.out.println("Главный поток прерван.");
        }

        System.out.println("Итоговый баланс: " + account.getBalance());
    }

    private static Thread getThread(Account account) {
        Random random = new Random();

        // Поток для случайного пополнения счета
        Thread depositThread = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                int amount = random.nextInt(1000) + 1; // Случайная сумма от 1 до 1000
                account.deposit(amount);
                try {
                    Thread.sleep(random.nextInt(1000)); // Пауза между пополнениями
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Поток пополнения прерван.");
                    return;
                }
            }
        });
        return depositThread;
    }
}