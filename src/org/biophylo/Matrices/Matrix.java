package org.biophylo.Matrices;

import org.biophylo.Listable;
import org.biophylo.Matrices.Datatype.Datatype;
import org.biophylo.Mediators.TaxaMediator;
import org.biophylo.Taxa.Taxa;
import org.biophylo.Taxa.TaxaLinker;
import org.biophylo.*;
import org.biophylo.Util.Exceptions.*;
import org.biophylo.Util.*;
import java.util.*;

public class Matrix extends Listable implements TypeSafeData, TaxaLinker {
	private Datatype typeObject;
	private static TaxaMediator taxaMediator = TaxaMediator.getInstance();
	private static Logger logger = Logger.getInstance();
	private Vector charlabels;
	
	public Matrix() {
		super();
		this.initialize("Standard");
	}	
	
	public Matrix(String type) {
		super();
		this.initialize(type);
	}	
	
	private void initialize (String type) {
		this.typeObject = Datatype.getInstance(type);
		this.container = CONSTANT.MATRICES;
		this.type = CONSTANT.MATRIX;
		this.charlabels = new Vector();	
		this.tag = "characters";
	}
	
	public Vector getCharLabels() {
		int nchar = this.getNchar();
		logger.info("nchar: "+nchar);
		if ( this.charlabels.capacity() < nchar ) {
			this.charlabels.ensureCapacity(nchar);
		}
		return this.charlabels;
	}
	
	public char getGap() {
		return this.getTypeObject().getGap();
	}

	public int[][] getLookup() {
		return this.getTypeObject().getLookup();
	}

	public char getMissing() {
		return this.getTypeObject().getMissing();
	}

	public String getType() {
		return this.getTypeObject().getType();
	}

	public Datatype getTypeObject() {
		return this.typeObject;
	}

	public void setGap(char gap) {
		this.getTypeObject().setGap(gap);
	}

	public void setLookup(int[][] lookup) {
		this.getTypeObject().setLookup(lookup);
	}
	
	public void setLookup(HashMap lookup) {
		this.getTypeObject().setLookup(lookup);
	}

	public void setMissing(char missing) {
		this.getTypeObject().setMissing(missing);
	}

	public void setTypeObject(Datatype typeObject) {
		this.typeObject = typeObject;
	}

	public Taxa getTaxa() {
		return (Taxa)taxaMediator.getLink(this.getId());
	}

	public void setTaxa(Taxa taxa) {
		taxaMediator.setLink(taxa.getId(), this.getId());
	}

	public void unsetTaxa() {
		taxaMediator.removeLink(-1, this.getId());
	}
	
	public int getNchar() {
		Containable[] rows = this.getEntities();
		int nchar = 0;
		for ( int i = 0; i < rows.length; i++ ) {
			String[] chars = ((Datum)rows[i]).getChar();
			if ( chars.length > nchar ) {
				nchar = chars.length;
			}
		}
		return nchar;
	}
	
	public boolean canContain(Object obj) {
		if ( this.type() != ((Containable)obj).container() ) {
			return false;
		}
		else {
			if ( ! this.getTypeObject().isSame( ((TypeSafeData)obj).getTypeObject() ) ) {
				return false;
			}
			else {
				return true;
			}
		}
	}
	
	public void insert(Object obj) throws ObjectMismatch {
		if ( this.canContain(obj) ) {
			((TypeSafeData)obj).setTypeObject(this.getTypeObject());
			super.insert(obj);
		}
		else {
			throw new ObjectMismatch();
		}
	}
	
	public void insert(Object[] obj) throws ObjectMismatch {
		for ( int i = 0; i < obj.length; i++ ) {
			this.insert(obj[i]);
		}
	}
	
	public String toXml () throws ObjectMismatch {
		return this.toXml(false);
	}
	
	public String toXml(boolean compact) throws ObjectMismatch {
		String type = this.getType();
		String xsi_type = compact ? "nex:"+type+"Seqs" : "nex:"+type+"Cells";
		HashMap idsForStates = null;
		this.setAttributes("xsi:type", xsi_type);
		StringBuffer sb = new StringBuffer();
		sb.append(this.getXmlTag(false));
		if ( ! compact ) {
			sb.append("<format>");
			Datatype to = this.getTypeObject();
			idsForStates = to.getIdsForStates();
			sb.append(to.toXml());
			if ( idsForStates != null ) {
				sb.append(this.writeCharLabels(to.getXmlId()));
			}
			else {
				sb.append(this.writeCharLabels(null));
			}
			sb.append("</format>");
		}
		sb.append("<matrix>");
		int nchar = this.getNchar();
		String[] charIds = new String[nchar];
		for ( int i = 0; i < nchar; i++ ) {
			charIds[i] = "c" + ( i + 1 );
		}
		Containable[] rows = this.getEntities();
		for ( int i = 0; i < rows.length; i++ ) {
			sb.append(((Datum)rows[i]).toXml(idsForStates,charIds,compact));
		}
		sb.append("</matrix>");
		sb.append("</");
		sb.append(this.getTag());
		sb.append('>');
		return sb.toString();		
	}
	
	private String writeCharLabels(String statesId) {
		StringBuffer sb = new StringBuffer();
		Vector labels = this.getCharLabels();
		int nchar = this.getNchar();
		for ( int i = 1; i <= nchar; i++ ) {
			String label = null;
			if ( ! labels.isEmpty() ) {
				label = (String)labels.get(i-1);
			}
			sb.append("<char id=\"c");
			sb.append(i);
			if ( statesId != null ) {
				sb.append("\" states=\"");
				sb.append(statesId);
			}
			if ( label != null ) {
				sb.append("\" label=\"");
				sb.append(label);
			}
			sb.append("\"/>");
		}
		return sb.toString();
	}

}