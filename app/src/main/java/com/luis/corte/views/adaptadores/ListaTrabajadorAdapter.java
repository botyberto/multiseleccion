package com.luis.corte.views.adaptadores;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.luis.corte.Controlers.Controlador;
import com.luis.corte.R;
import com.luis.corte.complementos.Complementos;
import com.luis.corte.complementos.FileLog;
import com.luis.corte.models.Configuracion;
import com.luis.corte.models.Producto;
import com.luis.corte.views.dialogForm.CapturaDialogDialog;
import com.luis.corte.views.dialogForm.DialogListaPuestos;
import com.luis.corte.views.dialogForm.InterfaceDialogs;
import com.luis.corte.models.CatalogoActividades;
import com.luis.corte.models.CatalogoCajas;
import com.luis.corte.models.CatalogoPuestos;
import com.luis.corte.models.Trabajadores;

import java.util.HashMap;
import java.util.Set;


/**
 * Created by josu on 3/9/2017.
 */

public class ListaTrabajadorAdapter extends ArrayAdapter<Trabajadores> implements InterfaceDialogs {

    private Context context;
    private Controlador controlador;
    private Trabajadores trabajadores;
    private TextView asistencia;
    private Class origen;
    private HashMap<Integer, Boolean> mSelection = new HashMap<Integer, Boolean>();

    public TextView consecutivo;
    public TextView trabajador;
    public TextView puesto;

    public ListaTrabajadorAdapter(Context context, Controlador controlador,TextView asistencia,Class origen) {
        super(context, R.layout.item_list_trabajadores, controlador.getListaTrabajadores());
        this.context = context;
        this.controlador = controlador;
        this.asistencia = asistencia;
        this.origen = origen;
    }



    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView==null){
            convertView = inflater.inflate(R.layout.item_list_trabajadores, parent, false);
        }

        try {
            final Trabajadores empleado = controlador.getTrabajador(position);

            consecutivo = (TextView) convertView.findViewById(R.id.tv_consecutivo);
            trabajador = (TextView) convertView.findViewById(R.id.tv_trabajador);
            puesto = (TextView) convertView.findViewById(R.id.tv_puesto);

            consecutivo.setText(empleado.getConsecutivo().toString());
            trabajador.setText(empleado.getTrabajador());
            puesto.setText(empleado.getPuestosActual().getDescripcion());

            convertView.setBackgroundColor(Color.parseColor("#ffffff")); //default color
            if (mSelection.get(position) != null) {
                convertView.setBackgroundColor(controlador.getActivity().getResources().getColor(R.color.colorAccent));// this is a selected position so make it red
            }

        }catch (Exception e){
            System.out.println("error "+e.getCause());
        }

        return convertView;
    }

    public void setNewSelection(int position, boolean value) {
        mSelection.put(position, value);
        notifyDataSetChanged();
    }

    public Set<Integer> getCurrentCheckedPosition() {
        return mSelection.keySet();
    }

    public void removeSelection(int position) {

        mSelection.remove(position);
        notifyDataSetChanged();
    }

    public void clearSelection() {
        mSelection = new HashMap<Integer, Boolean>();
        notifyDataSetChanged(); }



    @Override
    public void add(@Nullable Trabajadores object) {
        object.setConsecutivo(this.getCount()+1);
        Controlador.TiposError tieposError = controlador.setTrabajador(object);
        Complementos.mensajesError(controlador.getActivity(),tieposError);
    }

    @Nullable
    @Override
    public Trabajadores getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

/*
    public void getSelectedItem(MenuItem item){

            switch (item.getTitle().toString()){
                case "EDITAR TRABAJADOR":
                    Controlador.TiposError tiposError = controlador.validarInicioSesion();
                    if(tiposError== Controlador.TiposError.SESION_INICIADA || tiposError== Controlador.TiposError.SESION_REINIADA){
                        new CapturaDialogDialog(controlador, ListaTrabajadorAdapter.this,trabajadores, CapturaDialogDialog.tiposDialogos.DIALOG_ADD_TRABAJADOR);
                    }else{
                        Complementos.mensajesError(controlador.getActivity(),Controlador.TiposError.SESION_FINALIZADA);
                    }
                    break;
                case "PUESTOS REALIZADOS":
                    DialogListaPuestos dmp = new DialogListaPuestos(controlador.getActivity());
                    dmp.setPuestos(controlador.getPuestosTrabajador(trabajadores),trabajadores,this.controlador,ListaTrabajadorAdapter.this);
                    dmp.show();
                    break;
            }

    }*/


    public void actualizarAdapter(){
        ListaTrabajadorAdapter.this.notifyDataSetChanged();
        asistencia.setText("Asistencia: "+controlador.totalAsistencia());
    }

    public Controlador.TiposError capturarModificacionTrabajador(Trabajadores trabajadores,Trabajadores trabajdorAnterior){
        //actualizar trabajador
        FileLog.i(Complementos.TAG_DIALOGOS,"inicia actualizacion de trabajador "+trabajadores.toString());
        Controlador.TiposError tiposError = controlador.updateTrabajadores(trabajadores, trabajdorAnterior);
        if(tiposError==Controlador.TiposError.EXITOSO){
            tiposError = controlador.updateNombreTrabajadorPuesto(trabajadores);

            if(tiposError==Controlador.TiposError.EXITOSO){
                actualizarAdapter();
                tiposError = controlador.updateNombreTrabajadorProduccion(trabajadores);
            }
        }

        return tiposError;
    }

    /////////////////////interface/////////////////////
    @Override
    public void onDialogPositiveClickCapturaPuestos(String descripcion) {
    //no implementar
    }

    @Override
    public void onDialogPositiveClickCapturaTrabajadores(Trabajadores trabajadores,Trabajadores trabajdorAnterior) {
        Complementos.mensajesError(controlador.getActivity(),capturarModificacionTrabajador(trabajadores,trabajdorAnterior));
    }

    @Override
    public void onDialogPositiveClickCapturaActividad(String descripcion, CatalogoCajas TamanioCaja) {
//no implementar
    }

    @Override
    public void onDialogPositiveClickSeleccionActividad(CatalogoActividades catalogoActividades)
    {
//no implementar
    }

    @Override
    public void onDialogPositiveClickCambiarPuesto(Integer consecutivo, CatalogoPuestos catalogoPuestos, String horaCambio) {

        Controlador.TiposError tiposError = controlador.cambiarPuesto(consecutivo, catalogoPuestos, horaCambio);

        if(tiposError==Controlador.TiposError.EXITOSO){
            ListaTrabajadorAdapter.this.notifyDataSetChanged();
            asistencia.setText("Asistencia: "+controlador.totalAsistencia());
        }else{
            Complementos.mensajesError(controlador.getActivity(),tiposError);
        }
    }

    @Override
    public void onDialogPositiveClickCambiarActividad(CatalogoActividades catalogoActividades) {
        //npo implementar
    }


    @Override
    public void onDialogPositiveClickCapturarProduccion(Producto producto, Boolean addProduccion) {
//no implementar
    }

    @Override
    public void onDialogPositiveClickCapturaTamanioCajas(String descripcion, Integer cantidad) {
        //no implementar.
    }

    @Override
    public void onDialogPositiveClickFinalizarJornada() {
        //no implementar
    }

    @Override
    public void onDialogPositiveClickConfiguracion(Configuracion configuracion, Configuracion configuracionAnterior) {
//no implementar
    }
}
