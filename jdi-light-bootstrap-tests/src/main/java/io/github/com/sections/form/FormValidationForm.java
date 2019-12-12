package io.github.com.sections.form;

import com.epam.jdi.light.elements.pageobjects.annotations.locators.UI;
import com.epam.jdi.light.ui.html.elements.common.Button;
import com.epam.jdi.light.ui.bootstrap.elements.composite.Form;
import com.epam.jdi.light.ui.html.elements.common.TextField;
import io.github.com.entities.SimpleContact;

@SuppressWarnings("unused")
public class FormValidationForm extends Form<SimpleContact> {

    @UI("#validated-form-name-field")
    private TextField name;
    @UI("#validated-form-email")
    private TextField email;
    @UI("#validated-form-phone")
    private TextField phone;

    @UI(".//button[@type='submit']")
    private Button submitBtn;

    @UI(".//button[@type='reset']")
    private Button clearBtn;

    public void reset() {
        this.clearBtn.click();
    }

    @Override
    public void submit() {
        this.submitBtn.click();
    }

    @Override
    public void fill(SimpleContact entity) {
        super.fill(entity);
        name.core().jsExecute("dispatchEvent(new Event('change'))"); //Form fill method doesn't fire necessary event
    }

}