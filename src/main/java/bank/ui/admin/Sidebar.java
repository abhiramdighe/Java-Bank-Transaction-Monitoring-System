package bank.ui.admin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Sidebar extends VBox {
    private final List<HBox> menuItems = new ArrayList<>();
    private final Consumer<String> onTabSelected;

    public Sidebar(Consumer<String> onTabSelected) {
        this.onTabSelected = onTabSelected;

        setStyle("-fx-background-color: #020617; -fx-border-color: #1e293b; -fx-border-width: 0 1 0 0;");
        setPrefWidth(240);
        setPadding(new Insets(30, 0, 20, 0));
        setSpacing(10);

        addMenuItem("📊", "Dashboard");
        addMenuItem("👥", "Users");
        addMenuItem("🗃", "Accounts");
        addMenuItem("💸", "Transactions");
        addMenuItem("📈", "Reports");
        
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        getChildren().add(spacer);
        
        addMenuItem("⚙", "Settings");

        // Mark first active
        if (!menuItems.isEmpty()) {
            setActive(menuItems.get(0), "Dashboard");
        }
    }

    private void addMenuItem(String icon, String label) {
        HBox box = new HBox(15);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(12, 12, 12, 25));

        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 16px;");
        
        Label textLbl = new Label(label);
        textLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-family: 'Poppins', 'Segoe UI'; -fx-font-size: 14px; -fx-font-weight: bold;");

        box.getChildren().addAll(iconLbl, textLbl);
        
        box.setOnMouseEntered(e -> {
            if (!box.getStyle().contains("-fx-border-color")) {
                box.setStyle("-fx-background-color: #1e293b; -fx-cursor: hand;");
            }
        });
        box.setOnMouseExited(e -> {
            if (!box.getStyle().contains("-fx-border-color")) {
                box.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
            }
        });

        box.setOnMouseClicked(e -> setActive(box, label));

        menuItems.add(box);
        getChildren().add(box);
    }

    private void setActive(HBox activeBox, String label) {
        for (HBox box : menuItems) {
            Label iLbl = (Label) box.getChildren().get(0);
            Label tLbl = (Label) box.getChildren().get(1);
            iLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 16px;");
            tLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-family: 'Poppins', 'Segoe UI'; -fx-font-size: 14px; -fx-font-weight: bold;");
            box.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        }

        activeBox.setStyle("-fx-background-color: #0f172a; -fx-border-color: #3b82f6; -fx-border-width: 0 0 0 4; -fx-cursor: hand;");
        ((Label) activeBox.getChildren().get(0)).setStyle("-fx-text-fill: #3b82f6; -fx-font-size: 16px;");
        ((Label) activeBox.getChildren().get(1)).setStyle("-fx-text-fill: #e2e8f0; -fx-font-family: 'Poppins', 'Segoe UI'; -fx-font-size: 14px; -fx-font-weight: bold;");

        onTabSelected.accept(label);
    }
}
