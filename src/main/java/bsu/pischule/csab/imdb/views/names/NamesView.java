package bsu.pischule.csab.imdb.views.names;

import bsu.pischule.csab.imdb.data.entity.Name;
import bsu.pischule.csab.imdb.data.service.NameService;
import bsu.pischule.csab.imdb.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;

import static bsu.pischule.csab.imdb.config.AppConfig.APP_LOCALE;
import static bsu.pischule.csab.imdb.config.AppConfig.DATE_TIME_FORMATTER;

@PageTitle("Люди")
@Route(value = "names/:nameID?/:action?(edit)", layout = MainLayout.class)
@Uses(Icon.class)
public class NamesView extends Div implements BeforeEnterObserver {

    private final String NAME_ID = "nameID";
    private final String NAME_EDIT_ROUTE_TEMPLATE = "names/%d/edit";

    private final Grid<Name> grid = new Grid<>(Name.class, false);
    private final Button cancel = new Button("Отмена");
    private final Button save = new Button("Сохранить");
    private final Button delete = new Button("Удалить");
    private final BeanValidationBinder<Name> binder;
    private final NameService nameService;
    private TextField firstName;
    private TextField lastName;
    private DatePicker dateOfBirth;
    private DatePicker dateOfDeath;
    private NumberField height;
    private IntegerField numberOfChildren;
    private Name name;

    public NamesView(NameService nameService) {
        this.nameService = nameService;
        addClassNames("names-view", "flex", "flex-col", "h-full");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn(Name::getFullName).setHeader("Имя");
        grid.addColumn(NamesView::formatDateOfBirth).setHeader("Дата рождения");
        grid.addColumn(NamesView::formatDateOfDeath).setHeader("Дата смерти");
        grid.addColumn(NamesView::formatHeight).setHeader("Рост");
        grid.addColumn("numberOfChildren").setHeader("Количество детей");
        grid.setItems(query ->
                nameService.list(
                                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                        .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(NAME_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(NamesView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Name.class);

        // Bind fields. This where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.name == null) {
                    this.name = new Name();
                }
                binder.writeBean(this.name);

                nameService.update(this.name);
                clearForm();
                refreshGrid();
                Notification.show("Запись о человеке сохранена.");
                UI.getCurrent().navigate(NamesView.class);
            } catch (ValidationException validationException) {
                Notification.show("Произошла ошибка при попытке сохранения записи о человеке");
            }
        });


        delete.addClickListener(e -> {
            try {
                Optional.ofNullable(name)
                        .map(Name::getId)
                        .ifPresent(nameService::delete);
                clearForm();
                refreshGrid();
                Notification.show("Запись о человеке удалена");
            } catch (DataIntegrityViolationException error) {
                Notification.show("Невозможно удалить запись о человеке, так как она используется в других таблицах");
            }

        });

    }

    private static String formatDateOfBirth(Name name) {
        return Optional.ofNullable(name.getDateOfBirth())
                .map(date -> date.format(DATE_TIME_FORMATTER))
                .orElse(null);
    }

    private static String formatDateOfDeath(Name name) {
        return Optional.ofNullable(name.getDateOfDeath())
                .map(date -> date.format(DATE_TIME_FORMATTER))
                .orElse(null);
    }

    private static String formatHeight(Name name) {
        return Optional.ofNullable(name.getHeight())
                .map("%.2f"::formatted)
                .orElse(null);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> samplePersonId = event.getRouteParameters().getInteger(NAME_ID);
        if (samplePersonId.isPresent()) {
            Optional<Name> samplePersonFromBackend = nameService.get(samplePersonId.get());
            if (samplePersonFromBackend.isPresent()) {
                populateForm(samplePersonFromBackend.get());
            } else {
                Notification.show(
                        String.format("Запрошенный человек не найден, ID = %d", samplePersonId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(NamesView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("flex flex-col");
        editorLayoutDiv.setWidth("400px");

        Div editorDiv = new Div();
        editorDiv.setClassName("p-l flex-grow");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        firstName = new TextField("Имя");
        lastName = new TextField("Фамилия");
        dateOfBirth = new DatePicker("Дата рождения", e -> dateOfDeath.setMin(e.getValue()));
        dateOfBirth.setLocale(APP_LOCALE);
        dateOfDeath = new DatePicker("Дата смерти", e -> dateOfBirth.setMax(e.getValue()));
        dateOfDeath.setLocale(APP_LOCALE);
        height = new NumberField("Рост");
        height.setStep(0.01);
        height.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                height.setValue(Math.round(e.getValue() * 100) / 100.0);
            }
        });
        numberOfChildren = new IntegerField("Количество детей");
        numberOfChildren.setHasControls(true);
        Component[] fields = new Component[]{
                firstName, lastName, dateOfBirth, dateOfDeath, height, numberOfChildren};

        for (Component field : fields) {
            ((HasStyle) field).addClassName("full-width");
        }
        formLayout.add(fields);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("w-full flex-wrap bg-contrast-5 py-s px-s");
        buttonLayout.setSpacing(true);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        buttonLayout.add(save, cancel, delete);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getLazyDataView().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Name value) {
        this.name = value;
        binder.readBean(this.name);
    }
}
