import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

public class CasosDeTesteTest {
    static final String LOGIN = "https://practicetestautomation.com/practice-test-login/";
    static final String FORM  = "https://www.selenium.dev/selenium/web/web-form.html";
    WebDriver driver;
    WebDriverWait wait;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }

    void fazerLogin(String usuario, String senha) {
        driver.get(LOGIN);
        driver.findElement(By.id("username")).sendKeys(usuario);
        driver.findElement(By.id("password")).sendKeys(senha);
        driver.findElement(By.id("submit")).click();
    }

    @Test
    void ct01_loginValidoRedireciona() {
        fazerLogin("student", "Password123");
        assertTrue(wait.until(ExpectedConditions.urlContains("logged-in-successfully")));
    }

    @Test
    void ct02_loginValidoExibeBoasVindas() {
        fazerLogin("student", "Password123");
        WebElement titulo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h1")));
        String textoPagina = driver.findElement(By.tagName("body")).getText();
        assertEquals("Logged In Successfully", titulo.getText());
        assertTrue(textoPagina.contains("Congratulations student"));
    }

    @ParameterizedTest
    @CsvSource({
        "incorrectUser, Password123, Your username is invalid!",
        "student, incorrectPassword, Your password is invalid!"
    })
    void ct03_e_ct04_loginInvalido(String usuario, String senha, String mensagemEsperada) {
        fazerLogin(usuario, senha);
        WebElement erro = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("error")));
        assertEquals(mensagemEsperada, erro.getText());
    }

    @Test
    void ct05_logout() {
        fazerLogin("student", "Password123");
        WebElement btnLogout = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Log out")));
        btnLogout.click();
        WebElement campoUsuario = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        assertTrue(campoUsuario.isDisplayed());
    }

    @Test
    void ct06_envioFormulario() {
        driver.get(FORM);
        driver.findElement(By.id("my-text-id")).sendKeys("Aluno UNIFIO");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        WebElement mensagem = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("message")));
        assertEquals("Received!", mensagem.getText());
    }

    @Test
    void ct07_camposBloqueados() {
        driver.get(FORM);
        WebElement disabledInput = driver.findElement(By.name("my-disabled"));
        WebElement readonlyInput = driver.findElement(By.name("my-readonly"));
        assertFalse(disabledInput.isEnabled());
        assertNotNull(readonlyInput.getAttribute("readonly"));
    }

    @Test
    void ct08_combobox() {
        driver.get(FORM);
        WebElement dropdownElement = driver.findElement(By.name("my-select"));
        Select select = new Select(dropdownElement);
        select.selectByVisibleText("Two");
        assertEquals("2", select.getFirstSelectedOption().getAttribute("value"));
    }

    @Test
    void ct09_checkboxes() {
        driver.get(FORM);
        WebElement check1 = driver.findElement(By.id("my-check-1"));
        WebElement check2 = driver.findElement(By.id("my-check-2"));
        assertTrue(check1.isSelected());
        assertFalse(check2.isSelected());
        check1.click();
        check2.click();
        assertFalse(check1.isSelected());
        assertTrue(check2.isSelected());
    }

    @Test
    void ct10_radioButtons() {
        driver.get(FORM);
        WebElement checkedRadio = driver.findElement(By.id("my-radio-1"));
        WebElement defaultRadio = driver.findElement(By.id("my-radio-2"));
        defaultRadio.click();
        assertTrue(defaultRadio.isSelected());
        assertFalse(checkedRadio.isSelected());
    }
}