package BUS;

import DAO.PositionDAO;
import DTO.PositionDTO;
import java.util.List;
import java.math.BigDecimal;
import java.util.ArrayList;

public class PositionBUS {
    private PositionDAO positionDAO;

    public PositionBUS() {
        positionDAO = new PositionDAO();
    }

    public List<PositionDTO> getAllPositions() {
        return positionDAO.getAllPositions();
    }

    public PositionDTO getPositionById(String positionId) {
        return positionDAO.getPositionById(positionId);
    }

    public boolean addPosition(String positionId, String positionName, BigDecimal salary) {
        PositionDTO position = new PositionDTO(positionId, positionName, salary);
        return positionDAO.addPosition(position);
    }

    public boolean addPosition(String positionId, String positionName, double salary) {
        return addPosition(positionId, positionName, BigDecimal.valueOf(salary));
    }

    public boolean addPosition(PositionDTO position) {
        return positionDAO.addPosition(position);
    }

    public boolean updatePosition(PositionDTO position) {
        return positionDAO.updatePosition(position);
    }

    public boolean deletePosition(String positionId) {
        return positionDAO.deletePosition(positionId);
    }

    public boolean standardizePositionId(String positionId) {
        if (positionId == null || positionId.isEmpty()) {
            return false;
        }

        // Nếu ID đã đúng định dạng "CV###", không cần chỉnh sửa
        if (positionId.matches("CV\\d{3}")) {
            return true;
        }

        // Nếu ID chỉ chứa các số
        if (positionId.matches("\\d+")) {
            int idNumber = Integer.parseInt(positionId);
            String newId = String.format("CV%03d", idNumber);
            return positionDAO.fixPositionId(positionId, newId);
        }

        return false;
    }

    public String generateNewPositionId() {
        List<PositionDTO> positions = positionDAO.getAllPositions();
        int maxId = 0;

        for (PositionDTO position : positions) {
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

    // Phương thức tìm kiếm chức vụ theo từ khóa
    public List<PositionDTO> searchPositions(String keyword) {
        List<PositionDTO> allPositions = getAllPositions();
        List<PositionDTO> results = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return allPositions;
        }

        String lowercaseKeyword = keyword.toLowerCase().trim();

        for (PositionDTO position : allPositions) {
            if (position.getPositionId().toLowerCase().contains(lowercaseKeyword) ||
                    position.getPositionName().toLowerCase().contains(lowercaseKeyword)) {
                results.add(position);
            }
        }

        return results;
    }

    /**
     * Kiểm tra xem mã chức vụ đã tồn tại trong cơ sở dữ liệu chưa
     * 
     * @param positionId mã chức vụ cần kiểm tra
     * @return true nếu mã đã tồn tại, false nếu chưa tồn tại
     */
    public boolean isDuplicatePositionId(String positionId) {
        return positionDAO.isDuplicatePositionId(positionId);
    }
}