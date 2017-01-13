/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sendsms;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import com.plivo.helper.api.client.RestAPI;
import com.plivo.helper.api.response.message.Message;
import com.plivo.helper.api.response.message.MessageResponse;
import com.plivo.helper.exception.PlivoException;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author HP240
 */
public class FXMLDocumentController implements Initializable {

    String TAG = "CONTROLLER";
    WebService plivo = new WebService();

    @FXML
    ComboBox listSMS;
    @FXML
    TextArea textMessage;
    @FXML
    TextField textOrden;
    @FXML
    TextField textNumero;
    @FXML
    Button btnEnviar;

    String HEADER_MESSAGE = "Hola Tu pedido KEMIK: ";
    String DIRECCION_CONTENT = " tiene conflicto en la direccion, puedes llamar al 23310356 para corregirlo.";
    String TARJETA_CONTENT = " no se pudo realizar el pago por medio de tarjeta, puedes llamar al 23310356 para corregirlo.";
    String ESPERA_CONTENT = " se realizara con pago en deposito, puedes enviarnos tu boleta al correo info@kemik.gt o https://www.facebook.com/kemikgt";
    String PASAR_CONTENT = " ya esta listo para que pases a traerlo. Te esperamos de lunes a viernes de 8:00 a 18:00 horas.";
    String LLAMA_CONTENT = " esta en ruta, mensajeria se ha comunicado pero no ha obtenido respuesta. Puedes llamar al 55381001 / 02 para recibirlo.";
    String MONTOS_CONTENT = " por superar el monto de Q1000 te solicitamos realizar anticipo del 25% para procesar tu pedido, puedes comunicarte al 23310356";
    String NUMERO_CONTENT = "KEMIK: Para poder ayudarte necesitamos tu número de pedido o a nombre de quien lo realizaste.";
    String REVISION_CONTENT = "KEMIK: Puedes pasar a nuestras oficinas para revisión del producto.";
    String GESTION_CONTENT = "KEMIK: Estamos gestionando tu garantía para poder darle una pronta solución.";
    
    
    String DIRECCION_HEADER = "Corregir dirección";
    String TARJETA_HEADER = "Error con tajeta";
    String ESPERA_HEADER = "Esperamos boleta";
    String PASAR_HEADER = "Pasar";
    String LLAMA_HEADER = "Conflicto en entrega";
    String MONTOS_HEADER = "Montos mayores";
    String NUMERO_HEADER = "Número";
    String REVISION_HEADER = "Revisión";
    String GESTION_HEADER = "En Proceso";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> items = FXCollections.observableArrayList("Corregir dirección", "Error con tarjeta", "Esperamos boleta", "Pasar", "Conflicto en entrega", "Montos mayores");
        listSMS.setItems(items);
        listSMS.valueProperty().addListener(onValueComboBoxChange());
        textMessage.setWrapText(true);
    }

    private ChangeListener onValueComboBoxChange() {
        return new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String newValue) {
                if (textOrden.getText().equals("")) {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setHeaderText("Por favor complete el campo orden");
                    alert.showAndWait();
                } else if (newValue.equals("Corregir dirección")) {
                    setMessage(textOrden.getText() + DIRECCION_CONTENT);
                } else if (newValue.equals("Error con tarjeta")) {
                    setMessage(textOrden.getText() + TARJETA_CONTENT);
                } else if (newValue.equals("Esperamos boleta")) {
                    setMessage(textOrden.getText() + ESPERA_CONTENT);
                } else if (newValue.equals("Pasar")) {
                    setMessage(textOrden.getText() + PASAR_CONTENT);
                } else if (newValue.equals("Conflicto en entrega")) {
                    setMessage(textOrden.getText() + LLAMA_CONTENT);
                } else if (newValue.equals("Montos mayores")) {
                    setMessage(textOrden.getText() + MONTOS_CONTENT);
                }
            }
        };
    }

    private void setMessage(String message) {
        textMessage.setText(HEADER_MESSAGE + message);
    }

    @FXML
    public void handleEnviar() {
        if (isCorrectInput()) {
            sendMessage(textNumero.getText(), textMessage.getText());
        }
    }

    private boolean isCorrectInput() {
        String mensaje = "";
        if (textNumero.getText().toString().isEmpty()) {
            mensaje += "Rellene el campo número";
        } else {
            try {
                Integer.parseInt(textNumero.getText().toString());
            } catch (NumberFormatException numberFormatException) {
                mensaje += "El número no puede contener letras";
            }
        }
        if (textMessage.getText().toString().isEmpty()) {
            mensaje += "\n Complete el campo mensaje";
        }
        if (textMessage.getText().toString().length()>160){
            mensaje += "\n El mensaje no puede contener más de 160 caracteres";
        }
        if (mensaje.isEmpty()) {
            return true;
        } else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setContentText(mensaje);
            alert.showAndWait();
            return false;
        }
    }

    private void sendMessage(String numero, String text) {
        NameValuePair telefono = new BasicNameValuePair("telefono", numero);
        NameValuePair texto = new BasicNameValuePair("texto", text);
        String response = plivo.post(plivo.URL_SMS, telefono, texto);
        parseResponse(response);
        /*Alert alert = new Alert(AlertType.INFORMATION);
        alert.setHeaderText("Mensaje enviado");
        alert.showAndWait();
        listSMS.setValue("MENSAJE");
        textMessage.setText("");
        textOrden.setText("");
        textNumero.setText("");*/
    }
    
    private void parseResponse(String response){
        if (response == null) {
            Alert alert = new Alert(AlertType.ERROR);
                alert.setHeaderText("Por favor verifique su conexión a Internet");
                alert.showAndWait();
        } else{
            try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(response);
            Long status = (Long) jsonObject.get("status");
            if (status == 200) {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setHeaderText("Mensaje enviado");
                alert.showAndWait();
                listSMS.setValue("MENSAJE");
                textMessage.setText("");
                textOrden.setText("");
                textNumero.setText("");
            } else {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Error: " + status);
                alert.setHeaderText("Código: " + jsonObject.get("code"));
                alert.setContentText((String) jsonObject.get("message"));
                alert.showAndWait();
            }
        } catch (ParseException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
    }
}
