package lab_4.javafx.utils;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Class that provides DSL-ish syntax for creating JavaFX GUI - it allows for inline
 * configuration of returned nodes.
 */
public final class FX {

	// High order not-nodes

	public static Stage stage(NodeInitializer<Stage> init) {
		return initAndReturn(new Stage(), init);
	}

	public static Scene scene(
			Parent root,
			NodeInitializer<Scene> init
	) {
		return initAndReturn(new Scene(root), init);
	}


	// Layout nodes

	public static AnchorPane anchorPane(LayoutPaneInitializer<AnchorPane> init) {
		return initWithChildrenAndReturn(new AnchorPane(), init);
	}

	public static BorderPane borderPane(LayoutPaneInitializer<BorderPane> init) {
		return initWithChildrenAndReturn(new BorderPane(), init);
	}

	public static FlowPane flowPane(LayoutPaneInitializer<FlowPane> init) {
		return initWithChildrenAndReturn(new FlowPane(), init);
	}

	public static GridPane gridPane(LayoutPaneInitializer<GridPane> init) {
		return initWithChildrenAndReturn(new GridPane(), init);
	}

	public static HBox hBox(LayoutPaneInitializer<HBox> init) {
		return initWithChildrenAndReturn(new HBox(), init);
	}

	public static StackPane stackPane(LayoutPaneInitializer<StackPane> init) {
		return initWithChildrenAndReturn(new StackPane(), init);
	}

	public static TilePane tilePane(LayoutPaneInitializer<TilePane> init) {
		return initWithChildrenAndReturn(new TilePane(), init);
	}

	public static VBox vBox(LayoutPaneInitializer<VBox> init) {
		return initWithChildrenAndReturn(new VBox(), init);
	}

	public static Region region(NodeInitializer<Region> init) {
		return initAndReturn(new Region(), init);
	}


	// Control nodes

	public static Accordion accordion(NodeInitializer<Accordion> init) {
		return initAndReturn(new Accordion(), init);
	}

	public static Alert alert(
			Alert.AlertType type,
			NodeInitializer<Alert> init
	) {
		return initAndReturn(new Alert(type), init);
	}

	public static Button button(NodeInitializer<Button> init) {
		return initAndReturn(new Button(), init);
	}

	public static ButtonBar buttonBar(NodeInitializer<ButtonBar> init) {
		return initAndReturn(new ButtonBar(), init);
	}

	public static <T> Cell<T> cell(NodeInitializer<Cell<T>> init) {
		return initAndReturn(new Cell<>(), init);
	}

	public static CheckBox checkBox(NodeInitializer<CheckBox> init) {
		return initAndReturn(new CheckBox(), init);
	}

	public static <T> CheckBoxTreeItem<T> checkBoxTreeItem(NodeInitializer<CheckBoxTreeItem<T>> init) {
		return initAndReturn(new CheckBoxTreeItem<>(), init);
	}

	public static CheckMenuItem checkMenuItem(NodeInitializer<CheckMenuItem> init) {
		return initAndReturn(new CheckMenuItem(), init);
	}

	public static <T> ChoiceBox<T> choiceBox(NodeInitializer<ChoiceBox<T>> init) {
		return initAndReturn(new ChoiceBox<>(), init);
	}

	public static <T> ChoiceDialog<T> choiceDialog(NodeInitializer<ChoiceDialog<T>> init) {
		return initAndReturn(new ChoiceDialog<>(), init);
	}

	public static ColorPicker colorPicker(NodeInitializer<ColorPicker> init) {
		return initAndReturn(new ColorPicker(), init);
	}

	public static <T> ComboBox<T> comboBox(NodeInitializer<ComboBox<T>> init) {
		return initAndReturn(new ComboBox<>(), init);
	}

	public static ContextMenu contextMenu(NodeInitializer<ContextMenu> init) {
		return initAndReturn(new ContextMenu(), init);
	}

	public static CustomMenuItem customMenuItem(NodeInitializer<CustomMenuItem> init) {
		return initAndReturn(new CustomMenuItem(), init);
	}

	public static DateCell dateCell(NodeInitializer<DateCell> init) {
		return initAndReturn(new DateCell(), init);
	}

	public static DatePicker datePicker(NodeInitializer<DatePicker> init) {
		return initAndReturn(new DatePicker(), init);
	}

	public static <R> Dialog<R> dialog(NodeInitializer<Dialog<R>> init) {
		return initAndReturn(new Dialog<>(), init);
	}

	public static DialogPane dialogPane(NodeInitializer<DialogPane> init) {
		return initAndReturn(new DialogPane(), init);
	}

	public static Hyperlink hyperlink(NodeInitializer<Hyperlink> init) {
		return initAndReturn(new Hyperlink(), init);
	}

	public static Label label(NodeInitializer<Label> init) {
		return initAndReturn(new Label(), init);
	}

	public static <T> ListCell<T> listCell(NodeInitializer<ListCell<T>> init) {
		return initAndReturn(new ListCell<>(), init);
	}

	public static <T> ListView<T> listView(NodeInitializer<ListView<T>> init) {
		return initAndReturn(new ListView<>(), init);
	}

	public static Menu menu(NodeInitializer<Menu> init) {
		return initAndReturn(new Menu(), init);
	}

	public static MenuBar menuBar(NodeInitializer<MenuBar> init) {
		return initAndReturn(new MenuBar(), init);
	}

	public static MenuButton menuButton(NodeInitializer<MenuButton> init) {
		return initAndReturn(new MenuButton(), init);
	}

	public static MenuItem menuItem(NodeInitializer<MenuItem> init) {
		return initAndReturn(new MenuItem(), init);
	}

	public static Pagination pagination(NodeInitializer<Pagination> init) {
		return initAndReturn(new Pagination(), init);
	}

	public static PasswordField passwordField(NodeInitializer<PasswordField> init) {
		return initAndReturn(new PasswordField(), init);
	}

	public static PopupControl popupControl(NodeInitializer<PopupControl> init) {
		return initAndReturn(new PopupControl(), init);
	}

	public static ProgressBar progressBar(NodeInitializer<ProgressBar> init) {
		return initAndReturn(new ProgressBar(), init);
	}

	public static ProgressIndicator progressIndicator(NodeInitializer<ProgressIndicator> init) {
		return initAndReturn(new ProgressIndicator(), init);
	}

	public static RadioButton radioButton(NodeInitializer<RadioButton> init) {
		return initAndReturn(new RadioButton(), init);
	}

	public static RadioMenuItem radioMenuItem(NodeInitializer<RadioMenuItem> init) {
		return initAndReturn(new RadioMenuItem(), init);
	}

	public static ScrollBar scrollBar(NodeInitializer<ScrollBar> init) {
		return initAndReturn(new ScrollBar(), init);
	}

	public static ScrollPane scrollPane(NodeInitializer<ScrollPane> init) {
		return initAndReturn(new ScrollPane(), init);
	}

	public static Separator separator(NodeInitializer<Separator> init) {
		return initAndReturn(new Separator(), init);
	}

	public static SeparatorMenuItem separatorMenuItem(NodeInitializer<SeparatorMenuItem> init) {
		return initAndReturn(new SeparatorMenuItem(), init);
	}

	public static Slider slider(NodeInitializer<Slider> init) {
		return initAndReturn(new Slider(), init);
	}

	public static <T> Spinner<T> spinner(NodeInitializer<Spinner<T>> init) {
		return initAndReturn(new Spinner<>(), init);
	}

	public static SplitMenuButton splitMenuButton(NodeInitializer<SplitMenuButton> init) {
		return initAndReturn(new SplitMenuButton(), init);
	}

	public static SplitPane splitPane(NodeInitializer<SplitPane> init) {
		return initAndReturn(new SplitPane(), init);
	}

	public static SplitPane.Divider splitPaneDivider(NodeInitializer<SplitPane.Divider> init) {
		return initAndReturn(new SplitPane.Divider(), init);
	}

	public static Tab tab(NodeInitializer<Tab> init) {
		return initAndReturn(new Tab(), init);
	}

	public static <S, T> TableCell<S, T> tableCell(NodeInitializer<TableCell<S, T>> init) {
		return initAndReturn(new TableCell<S, T>(), init);
	}

	public static <S, T> TableColumn<S, T> tableColumn(NodeInitializer<TableColumn<S, T>> init) {
		return initAndReturn(new TableColumn<>(), init);
	}

	public static <T> TableRow<T> tableRow(NodeInitializer<TableRow<T>> init) {
		return initAndReturn(new TableRow<>(), init);
	}

	public static <S> TableView<S> tableView(NodeInitializer<TableView<S>> init) {
		return initAndReturn(new TableView<>(), init);
	}

	public static TabPane tabPane(NodeInitializer<TabPane> init) {
		return initAndReturn(new TabPane(), init);
	}

	public static TextArea textArea(NodeInitializer<TextArea> init) {
		return initAndReturn(new TextArea(), init);
	}

	public static TextField textField(NodeInitializer<TextField> init) {
		return initAndReturn(new TextField(), init);
	}

	public static TextInputDialog textInputDialog(NodeInitializer<TextInputDialog> init) {
		return initAndReturn(new TextInputDialog(), init);
	}

	public static TitledPane titledPane(NodeInitializer<TitledPane> init) {
		return initAndReturn(new TitledPane(), init);
	}

	public static ToggleButton toggleButton(NodeInitializer<ToggleButton> init) {
		return initAndReturn(new ToggleButton(), init);
	}

	public static ToggleGroup toggleGroup(NodeInitializer<ToggleGroup> init) {
		return initAndReturn(new ToggleGroup(), init);
	}

	public static ToolBar toolBar(NodeInitializer<ToolBar> init) {
		return initAndReturn(new ToolBar(), init);
	}

	public static Tooltip tooltip(NodeInitializer<Tooltip> init) {
		return initAndReturn(new Tooltip(), init);
	}

	public static <T> TreeCell<T> treeCell(NodeInitializer<TreeCell<T>> init) {
		return initAndReturn(new TreeCell<>(), init);
	}

	public static <T> TreeItem<T> treeItem(NodeInitializer<TreeItem<T>> init) {
		return initAndReturn(new TreeItem<>(), init);
	}

	public static <S, T> TreeTableCell<S, T> treeTableCell(NodeInitializer<TreeTableCell<S, T>> init) {
		return initAndReturn(new TreeTableCell<>(), init);
	}

	public static <S, T> TreeTableColumn<S, T> treeTableColumn(NodeInitializer<TreeTableColumn<S,
			T>> init) {
		return initAndReturn(new TreeTableColumn<>(), init);
	}

	public static <T> TreeTableRow<T> treeTableRow(NodeInitializer<TreeTableRow<T>> init) {
		return initAndReturn(new TreeTableRow<>(), init);
	}

	public static <S> TreeTableView<S> treeTableView(NodeInitializer<TreeTableView<S>> init) {
		return initAndReturn(new TreeTableView<>(), init);
	}

	public static <T> TreeView<T> treeView(NodeInitializer<TreeView<T>> init) {
		return initAndReturn(new TreeView<>(), init);
	}


	// Privates

	private static <N> N initAndReturn(
			N node,
			NodeInitializer<N> init
	) {
		init.init(node);
		return node;
	}

	private static <P extends Pane> P initWithChildrenAndReturn(
			P node,
			LayoutPaneInitializer<P> init
	) {
		init.init(node, node.getChildren());
		return node;
	}

	/**
	 * Configures newly created node.
	 * <p>
	 * This is a {@linkplain java.util.function functional interface} whose functional method is
	 * {@link NodeInitializer#init(N)}
	 * @param <N> Type of node, or other element
	 */
	@FunctionalInterface
	public interface NodeInitializer<N> {

		/**
		 * Configures newly created node.
		 * @param node A node to configure.
		 */
		void init(N node);

		/**
		 * Indicates, that no additional configuration is needed.
		 */
		static <N> void noInit(N n) {}
	}
	/**
	 * Configures newly created pane.
	 * <p>
	 * This is a {@linkplain java.util.function functional interface} whose functional method is
	 * {@link LayoutPaneInitializer#init(P, ObservableList)}
	 * @param <P> Type of pane.
	 */
	@FunctionalInterface
	public interface LayoutPaneInitializer<P extends Pane> {

		/**
		 * Configures newly created pane.
		 * @param node A pane to configure.
		 * @param children By contract, it should be a reference to <code>node.getChildren()</code>
		 */
		void init(P node, ObservableList<Node> children);

		/**
		 * Indicates, that no additional configuration is needed.
		 */
		static <P extends Pane> void noInit(P p, ObservableList<Node> c) {}
	}
}
