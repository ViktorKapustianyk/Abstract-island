package org.example.creators;
import org.example.entity.map.Cell;
import org.example.entity.organism.Organism;
import org.example.entity.organism.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OrganismPopulationManager {
    private Cell[][] cells;
    private OrganismFactory organismFactory = new OrganismFactory();;
    public void populate(Cell[][] cells, List<OrganismInfo> organismInfoList) throws InstantiationException, IllegalAccessException {
        for (OrganismInfo organismInfo : organismInfoList) {
            Type type = Type.valueOf(organismInfo.getType().toUpperCase());

            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[i].length; j++) {
                    Cell cell = cells[i][j];
                    Map<Type, Set<Organism>> residents = cell.getResidents();
                    Set<Organism> tribe = residents.computeIfAbsent(type, k -> new HashSet<>());

                    for (int k = 0; k < organismInfo.getMaxNumPerCell() / 2; k++) {
                        Organism organism = organismFactory.createOrganism(organismInfo);
                        tribe.add(organism);
                        organism.setCell(cell);
                        organism.setCurrentX(i);
                        organism.setCurrentY(j);
                    }
                }
            }
        }
    }
}
