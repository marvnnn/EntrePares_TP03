package entidades;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import aed3.InterfaceEntidade;

public class Curso implements InterfaceEntidade {

    public static final short ATIVO_INSCRICOES = 0;
    public static final short ATIVO_SEM_INSCRICOES = 1;
    public static final short CONCLUIDO = 2;
    public static final short CANCELADO = 3;

    private int id;
    private int idUsuario;
    private String nome;
    private LocalDate dataInicio;
    private String descricao;
    private String codigoCompartilhavel;
    private short estado;

    public Curso() {
        this(-1, -1, "", LocalDate.now(), "", "", ATIVO_INSCRICOES);
    }

    public Curso(int idUsuario, String nome, LocalDate dataInicio, String descricao, String codigo, short estado) {
        this(-1, idUsuario, nome, dataInicio, descricao, codigo, estado);
    }

    public Curso(int id, int idUsuario, String nome, LocalDate dataInicio, String descricao, String codigo, short estado) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.dataInicio = dataInicio;
        this.descricao = descricao;
        this.codigoCompartilhavel = codigo;
        this.estado = estado;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate data) {
        this.dataInicio = data;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCodigoCompartilhavel() {
        return codigoCompartilhavel;
    }

    public void setCodigoCompartilhavel(String codigo) {
        this.codigoCompartilhavel = codigo;
    }

    public short getEstado() {
        return estado;
    }

    public void setEstado(short estado) {
        this.estado = estado;
    }

    public String getEstadoDescricao() {
        switch (estado) {
            case 0: return "Curso ativo e recebendo inscrições";
            case 1: return "Curso ativo, mas sem novas inscrições";
            case 2: return "Curso realizado e concluído";
            case 3: return "Curso cancelado";
            default: return "Estado desconhecido";
        }
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return "CÓDIGO........: " + codigoCompartilhavel +
               "\nNOME..........: " + nome +
               "\nDESCRIÇÃO.....: " + descricao +
               "\nDATA DE INÍCIO: " + dataInicio.format(formatter) +
               "\nESTADO........: " + getEstadoDescricao();
    }

    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(id);
        dos.writeInt(idUsuario);
        dos.writeUTF(nome);
        dos.writeInt((int) dataInicio.toEpochDay());
        dos.writeUTF(descricao);
        dos.writeUTF(codigoCompartilhavel);
        dos.writeShort(estado);
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] vb) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(vb);
        DataInputStream dis = new DataInputStream(bais);
        id = dis.readInt();
        idUsuario = dis.readInt();
        nome = dis.readUTF();
        dataInicio = LocalDate.ofEpochDay(dis.readInt());
        descricao = dis.readUTF();
        codigoCompartilhavel = dis.readUTF();
        estado = dis.readShort();
    }
}
