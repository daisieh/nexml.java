package mesquite.nexml.InterpretNEXML.NexmlReaders;

import java.util.ArrayList;
import java.util.List;

import mesquite.lib.*;
import mesquite.nexml.InterpretNEXML.AnnotationHandlers.TSSHandler;
import mesquite.nexml.InterpretNEXML.NexmlMesquiteManager;
import mesquite.nexml.InterpretNEXML.AnnotationHandlers.AnnotationWrapper;
import mesquite.nexml.InterpretNEXML.AnnotationHandlers.PredicateHandler;
import mesquite.nexml.InterpretNEXML.Constants;

import org.nexml.model.Annotatable;
import org.nexml.model.Annotation;
import org.nexml.model.Document;
import org.nexml.model.Matrix;
import org.nexml.model.OTUs;
import org.nexml.model.TreeBlock;

public class NexmlReader extends NexmlMesquiteManager {

	/**
	 *
	 * @param employerEmployee
	 */
	public NexmlReader (EmployerEmployee employerEmployee) {
		super(employerEmployee);
	}

	/**
	 *
	 * @param xmlDocument
	 * @param mesProject
	 * @return
	 */
	public MesquiteProject fillProjectFromNexml(Document xmlDocument,MesquiteProject mesProject) {
        resetNamespaceHandlers();
		List<OTUs> xmlOTUsList = xmlDocument.getOTUsList();
		MesquiteFile mesFile = mesProject.getFile(0);
		// process taxa blocks
		NexmlOTUsBlockReader nobr = new NexmlOTUsBlockReader(getEmployerEmployee());
		List<Annotatable> xmlAnnoOTUsList = new ArrayList<Annotatable>();
		for ( OTUs xmlOTUs : xmlDocument.getOTUsList() ) {
			xmlAnnoOTUsList.add(xmlOTUs);
		}
		nobr.readBlocks(mesProject, mesFile, xmlAnnoOTUsList);

		for ( OTUs xmlOTUs : xmlOTUsList ) {

			// process tree blocks
			NexmlTreeBlockReader ntbr = new NexmlTreeBlockReader(getEmployerEmployee());
			debug("about to read trees...");
			List<Annotatable> xmlAnnoTreeBlockList = new ArrayList<Annotatable>();
			for ( TreeBlock xmlTreeBlock : xmlDocument.getTreeBlockList(xmlOTUs) ) {
				xmlAnnoTreeBlockList.add(xmlTreeBlock);
			}
			ntbr.readBlocks(mesProject, mesFile, xmlAnnoTreeBlockList);

			// process characters blocks
			NexmlCharactersBlockReader ncbr = new NexmlCharactersBlockReader(getEmployerEmployee());
			List<Annotatable> xmlCharactersBlockList = new ArrayList<Annotatable>();
			for ( Matrix<?> xmlMatrix : xmlDocument.getMatrices(xmlOTUs) ) {
				xmlCharactersBlockList.add(xmlMatrix);
			}
			ncbr.readBlocks(mesProject, mesFile, xmlCharactersBlockList);
		}
        TSSHandler tsshandler = (TSSHandler) getNamespaceHandler(Constants.TSSURI);
        if (tsshandler != null) {
            tsshandler.initializeGeneralSelectors(mesProject);
        }
        return mesProject;
	}

	/**
	 *
	 * @param mesAttachable
	 * @param mesAnnotatable
	 */
	protected void readWrappedAnnotations(Attachable mesAttachable,Annotatable mesAnnotatable) {
		for ( Annotation annotation : mesAnnotatable.getAllAnnotations() ) {
			String name = annotation.getProperty();
			if ( null == name || "".equals(name) ) {
				name = annotation.getRel();
			}
			AnnotationWrapper aw = new AnnotationWrapper();
			aw.setValue(annotation.getValue());
			aw.setPredicateNamespace(annotation.getPredicateNamespace());
			aw.setName(name);
			mesAttachable.attach(aw);
		}
	}
    /**
	 *
	 * @param mesAssociable
	 * @param xmlAnnotatable
	 * @param segmentCount
	 * @param mesListable
	 */
	protected void readAnnotations(Associable mesAssociable, Annotatable xmlAnnotatable,int segmentCount,Listable mesListable) {
		for ( Annotation xmlAnnotation : xmlAnnotatable.getAllAnnotations() ) {
			PredicateHandler handler = getNamespaceHandler(xmlAnnotatable,xmlAnnotation);
			if ( null == handler ) {
				handler = getPredicateHandler(xmlAnnotatable,xmlAnnotation);
			}
			Object convertedValue = handler.getValue();
			Object pred = handler.getPredicate();

 			// Adding annotations for interpreting TSS commands:
			handler.read(mesAssociable, mesListable, segmentCount);
			if ( pred.toString().contains("tss:")) {
//            if (handler instanceof TSSHandler) {

                convertedValue = handler.getValue();
 				if (convertedValue.equals(Constants.NO_RULE)) {
 					debug ("couldn't find " + pred.toString());
					// no rule specified
 				} else {
					String[] mesProps = convertedValue.toString().split(";");
					for (String prop : mesProps) {
                        if (prop.contains("tss:")) {
                            String[] propParts = prop.split(":");
                            String convertedProp = propParts[0];
                            String convertedVal = propParts[1];
                            NameReference mesNr = mesAssociable.makeAssociatedObjects(convertedProp);
                            mesNr.setNamespace(xmlAnnotation.getPredicateNamespace());
                            mesAssociable.setAssociatedObject(mesNr,segmentCount, convertedVal);
                        } else {
                            String[] propParts = prop.split(":");
                            String convertedProp = propParts[0];
                            String convertedVal = propParts[1];
                            NameReference mesNr = mesAssociable.makeAssociatedObjects(convertedProp);
                            mesNr.setNamespace(xmlAnnotation.getPredicateNamespace());
                            try {
                                mesAssociable.setAssociatedLong(mesNr,segmentCount,new Long(convertedVal));
                            } catch (Exception e) {
                                mesAssociable.setAssociatedBit(mesNr,segmentCount,Boolean.TRUE);
                            }
                        }
					}
				}
 			} else if ( convertedValue instanceof Boolean ) {
				NameReference mesNr = mesAssociable.makeAssociatedBits(handler.getPredicate());
				mesNr.setNamespace(xmlAnnotation.getPredicateNamespace());
				mesAssociable.setAssociatedBit(mesNr,segmentCount,(Boolean)convertedValue);
			} else if ( convertedValue instanceof Double ) {
				NameReference mesNr = mesAssociable.makeAssociatedDoubles(handler.getPredicate());
				mesNr.setNamespace(xmlAnnotation.getPredicateNamespace());
				mesAssociable.setAssociatedDouble(mesNr,segmentCount,(Double)convertedValue);
			} else if ( convertedValue instanceof Long ) {
				NameReference mesNr = mesAssociable.makeAssociatedLongs(handler.getPredicate());
				mesNr.setNamespace(xmlAnnotation.getPredicateNamespace());
				mesAssociable.setAssociatedLong(mesNr,segmentCount,(Long)convertedValue);
			} else if ( convertedValue instanceof Object ) {
				NameReference mesNr = mesAssociable.makeAssociatedObjects(handler.getPredicate());
				mesNr.setNamespace(xmlAnnotation.getPredicateNamespace());
				mesAssociable.setAssociatedObject(mesNr,segmentCount,convertedValue);
			}
		}
	}
}