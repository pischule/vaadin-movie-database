package bsu.pischule.csab.imdb.views.films;

import bsu.pischule.csab.imdb.data.entity.Film;
import bsu.pischule.csab.imdb.data.entity.Name;
import bsu.pischule.csab.imdb.data.service.FilmService;
import bsu.pischule.csab.imdb.data.service.NameService;
import bsu.pischule.csab.imdb.views.MainLayout;
import com.vaadin.componentfactory.MultipleSelect;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static bsu.pischule.csab.imdb.config.AppConfig.*;

@PageTitle("Фильмы")
@Route(value = "films/:filmID?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class FilmsView extends Div implements BeforeEnterObserver {

    private final String FILM_ID = "filmID";
    private final String FILM_EDIT_ROUTE_TEMPLATE = "films/%d/edit";

    private final Grid<Film> grid = new Grid<>(Film.class, false);

    private TextField name;
    private Select<Name> director;
    private IntegerField duration;
    private TextArea description;
    private NumberField rating;
    private DatePicker releaseDate;
    private MultipleSelect<Name> actors;

    private final Button cancel = new Button("Отмена");
    private final Button save = new Button("Сохранить");
    private final Button delete = new Button("Удалить");

    private final BeanValidationBinder<Film> binder;

    private Film film;

    private final FilmService filmService;
    private final NameService nameService;

    public FilmsView(FilmService filmService, NameService nameService) {
        this.filmService = filmService;
        this.nameService = nameService;
        addClassNames("names-view", "flex", "flex-col", "h-full");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("name").setHeader("Название");
        grid.addColumn(FilmsView::formatDirectorName).setSortProperty("director.firstName", "director.lastName").setHeader("Режиссер");
        grid.addColumn("description").setHeader("Описание");
        grid.addColumn("duration").setHeader("Продолжительность");
        grid.addColumn(new LocalDateRenderer<>(Film::getReleaseDate, DATE_FORMAT)).setSortProperty("releaseDate").setHeader("Дата выхода");
        grid.addColumn(new NumberRenderer<>(Film::getRating, "%.1f")).setSortProperty("rating").setHeader("Рейтинг");
        grid.addColumn(FilmsView::formatActors).setHeader("Актеры");

        grid.setItems(query -> filmService.list(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream()
                .filter(it -> !it.getDeleted()));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();
        grid.setMultiSort(true);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(FILM_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(FilmsView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Film.class);

        // Bind fields. This where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.film == null) {
                    this.film = new Film();
                }
                binder.writeBean(this.film);

                filmService.update(this.film);
                clearForm();
                refreshGrid();
                Notification.show("Запись о фильме сохранена.");
                UI.getCurrent().navigate(FilmsView.class);
            } catch (ValidationException validationException) {
                Notification.show("Произошла ошибка при попытке сохранить фильм");
            }
        });

        delete.addClickListener(e -> {
            try {
                Optional.ofNullable(film)
                        .map(Film::getId)
                        .ifPresent(filmService::delete);
                clearForm();
                refreshGrid();
                Notification.show("Фильм удален.");
            } catch (DataIntegrityViolationException error) {
                Notification.show("Невозможно удалить фильм, так как он используется в других записях.");
            }

        });
    }

    private static Object formatDirectorName(Film film) {
        return Optional.ofNullable(film.getDirector())
                .map(Name::getFullName)
                .orElse(null);
    }

    private static Object formatActors(Film film) {
        return film.getActors().stream()
                .map(Name::getFullName)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.joining(", "));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> samplFilmId = event.getRouteParameters().getInteger(FILM_ID);
        if (samplFilmId.isPresent()) {
            Optional<Film> samplePersonFromBackend = filmService.get(samplFilmId.get());
            if (samplePersonFromBackend.isPresent()) {
                populateForm(samplePersonFromBackend.get());
            } else {
                Notification.show(
                        String.format("Запрашиваемый фильм не найден, ID = %d", samplFilmId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(FilmsView.class);
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
        name = new TextField("Название");
        duration = new IntegerField("Продолжительность (мин)");
        director = new Select<>();
        director.setLabel("Режиссер");
        director.setItems(nameService.list(Pageable.unpaged()).getContent());
        director.setItemLabelGenerator(Name::getFullName);
        description = new TextArea("Описание");
        rating = new NumberField("Рейтинг");
        rating.setStep(0.1);
        rating.addValueChangeListener(e -> {
            if (rating.getValue() != null) {
                rating.setValue(Math.round(rating.getValue() * 10) / 10.0);
            }
        });
        releaseDate = new DatePicker("Дата выхода");
        releaseDate.setLocale(APP_LOCALE);
        actors = new MultipleSelect<>();
        actors.setItems(nameService.list(Pageable.unpaged()).getContent());
        actors.setItemLabelGenerator(Name::getFullName);
        actors.setLabel("Актеры");


        Component[] fields = new Component[]{
                name,
                director,
                duration,
                description,
                rating,
                releaseDate,
                actors
        };

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

    private void populateForm(Film value) {
        this.film = value;
        binder.readBean(this.film);

    }
}
