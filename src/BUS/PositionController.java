package BUS;

import DAO.PositionDAO;
import DTO.Position;
import java.util.List;
import java.math.BigDecimal;

public class PositionController {
    private PositionDAO positionDAO;

    public PositionController() {
        positionDAO = new PositionDAO();
    }

    public List<Position> getAllPositions() {
        return positionDAO.getAllPositions();
    }

    public Position getPositionById(String positionId) {
        return positionDAO.getPositionById(positionId);
    }

    public boolean addPosition(String positionId, String positionName, BigDecimal salary) {
        Position position = new Position(positionId, positionName, salary);
        return positionDAO.addPosition(position);
    }

    public boolean addPosition(String positionId, String positionName, double salary) {
        return addPosition(positionId, positionName, BigDecimal.valueOf(salary));
    }

    public boolean updatePosition(String positionId, String positionName, BigDecimal salary) {
        Position position = new Position(positionId, positionName, salary);
        return positionDAO.updatePosition(position);
    }

    public boolean updatePosition(String positionId, String positionName, double salary) {
        return updatePosition(positionId, positionName, BigDecimal.valueOf(salary));
    }

    public boolean deletePosition(String positionId) {
        return positionDAO.deletePosition(positionId);
    }

    public String generateNewPositionId() {
        List<Position> positions = positionDAO.getAllPositions();
        int maxId = 0;

        for (Position position : positions) {
            String idStr = position.getPositionId();
            if (idStr.startsWith("CV")) {
                try {
                    int id = Integer.parseInt(idStr.substring(2));
                    if (id > maxId) {
                        maxId = id;
                    }
                } catch (NumberFormatException e) {
                    // Ignore if not a number
                }
            }
        }

        return "CV" + String.format("%03d", maxId + 1);
    }
}