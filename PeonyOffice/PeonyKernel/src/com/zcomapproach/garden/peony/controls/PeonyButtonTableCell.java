/*
 * Copyright 2018 ZComApproach Inc.
 *
 * Licensed under multiple open source licenses involved in the project (the "Licenses");
 * you may not use this file except in compliance with the Licenses.
 * You may obtain copies of the Licenses at
 *
 *      http://www.zcomapproach.com/licenses
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zcomapproach.garden.peony.controls;

import java.util.function.Function;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

/**
 *
 * @author zhijun98
 */
public class PeonyButtonTableCell <S> extends TableCell<S, Button> {
    
    private static final GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");

    private final Button peonyTableCellButton;
    
    public PeonyButtonTableCell(String buttonText, Function< S, S> function) {
        //this.getStyleClass().add("action-button-table-cell");
        this.peonyTableCellButton = new Button(buttonText);
        this.peonyTableCellButton.setOnAction((ActionEvent e) -> {
            function.apply(getCurrentItem());
        });
        this.peonyTableCellButton.setMaxWidth(Double.MAX_VALUE);
    }
    
    public PeonyButtonTableCell(String buttonText, Node graphic, Tooltip tooltip, Function< S, S> function) {
        //this.getStyleClass().add("action-button-table-cell");
        if (graphic == null){
            this.peonyTableCellButton = new Button(buttonText);
        }else{
            this.peonyTableCellButton = new Button(buttonText, graphic);
        }
        if (tooltip != null){
            this.peonyTableCellButton.setTooltip(tooltip);
        }
        this.peonyTableCellButton.setOnAction((ActionEvent e) -> {
            function.apply(getCurrentItem());
        });
        this.peonyTableCellButton.setMaxWidth(Double.MAX_VALUE);
    }

    public Button getPeonyTableCellButton() {
        return peonyTableCellButton;
    }

    public S getCurrentItem() {
        return getTableView().getItems().get(getIndex());
    }

    private static Node createFontAwesomeIcon(FontAwesome.Glyph aGlyph, Color aColor){
        return fontAwesome.create(aGlyph.getChar()).color(aColor);
    }

    public static <S> Callback<TableColumn<S, Button>, TableCell<S, Button>> callbackForTableColumn(String label, Function<S, S> function) {
        return param -> new PeonyButtonTableCell<>(label, function);
    }

    public static <S> Callback<TableColumn<S, Button>, TableCell<S, Button>> callbackForTableColumn(String label, ImageView buttonImageView, Tooltip tooltip, Function<S, S> function) {
        return param -> new PeonyButtonTableCell<>(label, buttonImageView, tooltip, function);
    }

    public static <S> Callback<TableColumn<S, Button>, TableCell<S, Button>> callbackForTableColumn(String label, FontAwesome.Glyph aGlyph, Color aColor, Tooltip tooltip, Function<S, S> function) {
        return param -> new PeonyButtonTableCell<>(label, createFontAwesomeIcon(aGlyph, aColor), tooltip, function);
    }

    @Override
    public void updateItem(Button item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
        } else {                
            setGraphic(peonyTableCellButton);
        }
    }
}
