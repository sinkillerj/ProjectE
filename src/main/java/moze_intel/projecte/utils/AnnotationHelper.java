package moze_intel.projecte.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.mapper.EMCMapper;
import moze_intel.projecte.api.mapper.IEMCMapper;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.forgespi.language.ModFileScanData.AnnotationData;
import org.objectweb.asm.Type;

public class AnnotationHelper {

	private static final Type MAPPER_TYPE = Type.getType(EMCMapper.class);

	//Note: We don't bother caching this value because EMCMappingHandler#loadMappers caches our processed result
	public static List<IEMCMapper<NormalizedSimpleStack, Long>> getEMCMappers() {
		ModList modList = ModList.get();
		List<IEMCMapper<NormalizedSimpleStack, Long>> emcMappers = new ArrayList<>();
		for (ModFileScanData scanData : modList.getAllScanData()) {
			for (AnnotationData data : scanData.getAnnotations()) {
				if (MAPPER_TYPE.equals(data.getAnnotationType())) {
					Map<String, Object> annotationData = data.getAnnotationData();
					if (annotationData.containsKey("requiredMods")) {
						//Check if all the mods the EMCMapper wants to be loaded are loaded
						List<String> requiredMods = (List<String>) annotationData.get("requiredMods");
						if (requiredMods.stream().anyMatch(modid -> !modList.isLoaded(modid))) {
							PECore.debugLog("Skipped checking class {}, as its required mods ({}) are not loaded.", data.getMemberName(), Arrays.toString(requiredMods.toArray()));
							continue;
						}
					}
					//If all the mods were loaded then attempt to get the mapper
					IEMCMapper mapper = getEMCMapper(data.getMemberName());
					if (mapper != null) {
						try {
							emcMappers.add((IEMCMapper<NormalizedSimpleStack, Long>) mapper);
							PECore.LOGGER.info("Found and loaded EMC mapper: {}", mapper.getName());
						} catch (ClassCastException e) {
							PECore.LOGGER.error("{}: Is not a mapper for {}}, to {}", mapper.getClass(), NormalizedSimpleStack.class, Long.class, e);
						}
					}
				}
			}
		}
		return emcMappers;
	}

	@Nullable
	private static IEMCMapper getEMCMapper(String className) {
		//Try to create an instance of the class
		try {
			Class<? extends IEMCMapper> subClass = Class.forName(className).asSubclass(IEMCMapper.class);
			//First try looking at the fields of the class to see if one of them is specified as the instance
			Field[] fields = subClass.getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(EMCMapper.Instance.class)) {
					if (Modifier.isStatic(field.getModifiers())) {
						Object fieldValue = field.get(null);
						if (fieldValue instanceof IEMCMapper) {
							IEMCMapper mapper = (IEMCMapper) fieldValue;
							PECore.debugLog("Found specified EMC Mapper instance for: {}. Using it rather than creating a new instance.", mapper.getName());
							return mapper;
						} else {
							PECore.LOGGER.error("EMCMapperInstance annotation found on non IEMCMapper field: {}", field);
							return null;
						}
					} else {
						PECore.LOGGER.error("EMCMapperInstance annotation found on non static field: {}", field);
						return null;
					}
				}
			}
			//If we don't have any fields that have the EMCMapperInstance annotation, then try to create a new instance of the class
			return subClass.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | LinkageError e) {
			PECore.LOGGER.error("Failed to load: {}", className, e);
		}
		return null;
	}
}