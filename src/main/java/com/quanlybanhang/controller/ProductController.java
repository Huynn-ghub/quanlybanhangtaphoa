package com.quanlybanhang.controller;

import com.quanlybanhang.dao.CategoryDAO;
import com.quanlybanhang.dao.ProductDAO;
import com.quanlybanhang.model.Category;
import com.quanlybanhang.model.Product;
import com.quanlybanhang.view.ProductPanel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller điều khiển quản lý Sản phẩm.
 * Kết nối dữ liệu từ ProductDAO, CategoryDAO sang ProductPanel.
 * Hỗ trợ xuất/nhập danh sách sản phẩm từ file Excel.
 */
public class ProductController {
    private ProductPanel view;
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;
    private List<Product> currentProductList;

    public ProductController(ProductPanel view, ProductDAO productDAO, CategoryDAO categoryDAO) {
        this.view = view;
        this.productDAO = productDAO;
        this.categoryDAO = categoryDAO;

        initListeners();
    }

    private void initListeners() {
        view.addSearchListener(e -> searchProducts());
        view.addFilterCategoryListener(e -> filterByCategory());
        
        view.addAddListener(e -> addProduct());
        view.addEditListener(e -> editProduct());
        view.addDeleteListener(e -> deleteProduct());
        view.addClearListener(e -> view.clearForm());

        view.addManageCategoryListener(e -> openCategoryManager());
        view.addExportExcelListener(e -> exportToExcel());
        view.addImportExcelListener(e -> importFromExcel());

        view.addTableMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Product selected = view.getSelectedProduct();
                if (selected != null) {
                    Product fullDetail = productDAO.getById(selected.getId());
                    if (fullDetail != null) {
                        view.showProductOnForm(fullDetail);
                    }
                }
            }
        });
    }

    public void loadData() {
        List<Category> categories = categoryDAO.getAll();
        view.setCategories(categories);

        currentProductList = productDAO.getAll();
        view.setProductTableData(currentProductList);
        view.clearForm();
    }

    private void searchProducts() {
        String query = view.getSearchQuery();
        if (query.isEmpty()) {
            currentProductList = productDAO.getAll();
        } else {
            currentProductList = productDAO.search(query);
        }
        view.setProductTableData(currentProductList);
    }

    private void filterByCategory() {
        Category cat = view.getSelectedFilterCategory();
        if (cat == null) return;

        if (cat.getId() == 0) {
            currentProductList = productDAO.getAll();
        } else {
            List<Product> all = productDAO.getAll();
            List<Product> filtered = new ArrayList<>();
            for (Product p : all) {
                if (p.getCategoryId() == cat.getId()) {
                    filtered.add(p);
                }
            }
            currentProductList = filtered;
        }
        view.setProductTableData(currentProductList);
    }

    private void addProduct() {
        Product p = view.getProductFromForm();
        if (p == null) {
            JOptionPane.showMessageDialog(view, "Thông tin nhập liệu không hợp lệ. Vui lòng kiểm tra lại đơn giá, tồn kho!");
            return;
        }

        if (p.getCode().isEmpty() || p.getName().isEmpty() || p.getUnit().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập đầy đủ Mã sản phẩm, Tên sản phẩm, Đơn vị!");
            return;
        }

        if (productDAO.isCodeExists(p.getCode())) {
            JOptionPane.showMessageDialog(view, "Mã sản phẩm đã tồn tại trong hệ thống!");
            return;
        }

        if (productDAO.insert(p)) {
            JOptionPane.showMessageDialog(view, "Thêm sản phẩm thành công!");
            loadData();
        } else {
            JOptionPane.showMessageDialog(view, "Thêm sản phẩm thất bại.");
        }
    }

    private void editProduct() {
        Product selected = view.getSelectedProduct();
        if (selected == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn sản phẩm cần cập nhật từ danh sách!");
            return;
        }

        Product p = view.getProductFromForm();
        if (p == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng kiểm tra lại tính hợp lệ của các trường số!");
            return;
        }

        p.setId(selected.getId());
        if (productDAO.update(p)) {
            JOptionPane.showMessageDialog(view, "Cập nhật sản phẩm thành công!");
            loadData();
        } else {
            JOptionPane.showMessageDialog(view, "Cập nhật sản phẩm thất bại.");
        }
    }

    private void deleteProduct() {
        Product selected = view.getSelectedProduct();
        if (selected == null) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn sản phẩm cần ngưng bán!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, 
                "Bạn muốn ngừng bán sản phẩm này? (Không hiển thị trong màn hình bán hàng)", 
                "Xác nhận ngừng bán", 
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (productDAO.delete(selected.getId())) {
                JOptionPane.showMessageDialog(view, "Đã ngừng bán sản phẩm!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(view, "Cập nhật trạng thái thất bại.");
            }
        }
    }

    private void openCategoryManager() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(view), "Quản Lý Nhóm Sản Phẩm", true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(view);
        dialog.setLayout(new BorderLayout(10, 10));

        String[] cols = {"ID", "Tên nhóm sản phẩm", "Mô tả"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        
        Runnable refreshTable = () -> {
            model.setRowCount(0);
            for (Category c : categoryDAO.getAll()) {
                model.addRow(new Object[]{c.getId(), c.getName(), c.getDescription()});
            }
        };
        refreshTable.run();

        dialog.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JTextField txtName = new JTextField();
        JTextField txtDesc = new JTextField();
        inputPanel.add(new JLabel("Tên nhóm:"));
        inputPanel.add(txtName);
        inputPanel.add(new JLabel("Mô tả:"));
        inputPanel.add(txtDesc);
        dialog.add(inputPanel, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Thêm");
        JButton btnDel = new JButton("Xóa");
        
        btnAdd.addActionListener(e -> {
            String name = txtName.getText().trim();
            String desc = txtDesc.getText().trim();
            if (name.isEmpty()) return;
            Category c = new Category(0, name, desc);
            categoryDAO.insert(c);
            txtName.setText(""); txtDesc.setText("");
            refreshTable.run();
            view.setCategories(categoryDAO.getAll());
        });

        btnDel.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = (int) table.getValueAt(row, 0);
                categoryDAO.delete(id);
                refreshTable.run();
                view.setCategories(categoryDAO.getAll());
            }
        });

        btnPanel.add(btnAdd);
        btnPanel.add(btnDel);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void exportToExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("Danh_Sach_San_Pham.xlsx"));
        if (fileChooser.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (Workbook workbook = new XSSFWorkbook();
                 FileOutputStream fos = new FileOutputStream(file)) {
                
                Sheet sheet = workbook.createSheet("Sản Phẩm");

                Row header = sheet.createRow(0);
                String[] cols = {"Mã SP", "Tên SP", "Đơn giá mua", "Đơn giá bán", "Tồn kho", "Đơn vị"};
                for (int i = 0; i < cols.length; i++) {
                    Cell cell = header.createCell(i);
                    cell.setCellValue(cols[i]);
                }

                int rowIdx = 1;
                for (Product p : currentProductList) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(p.getCode());
                    row.createCell(1).setCellValue(p.getName());
                    row.createCell(2).setCellValue(p.getPurchasePrice());
                    row.createCell(3).setCellValue(p.getSalePrice());
                    row.createCell(4).setCellValue(p.getStockQuantity());
                    row.createCell(5).setCellValue(p.getUnit());
                }

                workbook.write(fos);
                JOptionPane.showMessageDialog(view, "Xuất Excel thành công!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Lỗi xuất Excel: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void importFromExcel() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (FileInputStream fis = new FileInputStream(file);
                 Workbook workbook = new XSSFWorkbook(fis)) {
                
                Sheet sheet = workbook.getSheetAt(0);
                int count = 0;
                
                List<Category> cats = categoryDAO.getAll();
                int defaultCatId = cats.isEmpty() ? 1 : cats.get(0).getId();

                for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                    Row row = sheet.getRow(r);
                    if (row == null) continue;
                    
                    String code = row.getCell(0).getStringCellValue();
                    String name = row.getCell(1).getStringCellValue();
                    double buy = row.getCell(2).getNumericCellValue();
                    double sell = row.getCell(3).getNumericCellValue();
                    int stock = (int) row.getCell(4).getNumericCellValue();
                    String unit = row.getCell(5).getStringCellValue();

                    if (productDAO.isCodeExists(code)) {
                        continue;
                    }

                    Product p = new Product(0, code, name, defaultCatId, buy, sell, stock, unit, "", true);
                    productDAO.insert(p);
                    count++;
                }

                JOptionPane.showMessageDialog(view, "Đã nhập thành công " + count + " sản phẩm từ Excel!");
                loadData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Lỗi đọc file Excel: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
