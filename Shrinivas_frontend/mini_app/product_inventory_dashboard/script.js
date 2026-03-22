const dummyProducts = [
  { id: 1, name: "Laptop", price: 55000, stock: 5, category: "electronics" },
  {
    id: 2,
    name: "Smartphone",
    price: 20000,
    stock: 3,
    category: "electronics",
  },
  {
    id: 3,
    name: "Headphones",
    price: 1500,
    stock: 10,
    category: "electronics",
  },
  { id: 4, name: "T-Shirt", price: 500, stock: 20, category: "clothing" },
  { id: 5, name: "Jeans", price: 1200, stock: 0, category: "clothing" },
  { id: 6, name: "JavaScript Book", price: 800, stock: 7, category: "books" },
  { id: 7, name: "Notebook", price: 100, stock: 2, category: "books" },
  { id: 8, name: "Backpack", price: 1500, stock: 6, category: "accessories" },
  { id: 9, name: "Watch", price: 2500, stock: 1, category: "accessories" },
  { id: 10, name: "Sunglasses", price: 700, stock: 0, category: "accessories" },
  { id: 11, name: "Tablet", price: 30000, stock: 4, category: "electronics" },
  {
    id: 12,
    name: "Bluetooth Speaker",
    price: 2500,
    stock: 8,
    category: "electronics",
  },
  { id: 13, name: "Hoodie", price: 900, stock: 6, category: "clothing" },
  { id: 14, name: "Jacket", price: 2000, stock: 2, category: "clothing" },
  { id: 15, name: "Python Book", price: 850, stock: 5, category: "books" },
  { id: 16, name: "DSA Book", price: 950, stock: 1, category: "books" },
  { id: 17, name: "Wallet", price: 600, stock: 9, category: "accessories" },
  { id: 18, name: "Belt", price: 400, stock: 3, category: "accessories" },
];

let products = [];

let productContainer = document.getElementById("product-container");
let noProductsMessage = document.getElementById("no-products-message");

function showMessage(message) {
  noProductsMessage.style.display = "block";
  noProductsMessage.innerText = message;
}

function hideMessage() {
  noProductsMessage.style.display = "none";
}

let currentPage = 1;
let itemsPerPage = 6;

// Pagination function
function paginate(data, page = 1, perPage = 6) {
  const start = (page - 1) * perPage;
  return data.slice(start, start + perPage);
}

// Render products with pagination
function renderProducts(data) {
  productContainer.innerHTML = "";

  if (data.length === 0) {
    showMessage("No products found matching your criteria.");
    paginationControls.innerHTML = "";
    return;
  } else {
    hideMessage();
  }

  const paginatedData = paginate(data, currentPage, itemsPerPage);

  let html = "";
  for (let product of paginatedData) {
    html += `
      <div class="product-card">
        <h2>${product.name}</h2>
        <h3>Price: ₹${product.price}</h3>
        <h3>Stock: ${product.stock}</h3>
        <h3>Category: ${product.category}</h3>
        <button data-id="${product.id}" data-action="delete">Delete</button>
        <button data-id="${product.id}" data-action="edit">Edit</button>
      </div>
    `;
  }

  productContainer.innerHTML = html;

  renderPaginationControls(data.length);
}

// Pagination controls
let paginationControls = document.getElementById("pagination-controls");

function renderPaginationControls(totalItems) {
  const totalPages = Math.ceil(totalItems / itemsPerPage);
  paginationControls.innerHTML = "";

  if (totalPages <= 1) return;

  for (let i = 1; i <= totalPages; i++) {
    const btn = document.createElement("button");
    btn.innerText = i;
    btn.className = i === currentPage ? "active-page" : "";
    btn.addEventListener("click", () => {
      currentPage = i;
      applyFilter(false); // false = do NOT reset currentPage
    });
    paginationControls.appendChild(btn);
  }
}

// Filters and sorting
let searchInput = document.getElementById("search-input");
let categoryFilter = document.getElementById("category-filter");
let stockFilter = document.getElementById("stock-filter");
let sortOption = document.getElementById("sort-option");

searchInput.addEventListener("input", () => applyFilter(true));
categoryFilter.addEventListener("change", () => applyFilter(true));
stockFilter.addEventListener("change", () => applyFilter(true));
sortOption.addEventListener("change", () => applyFilter(true));

searchInput.disabled = true;
categoryFilter.disabled = true;
stockFilter.disabled = true;
sortOption.disabled = true;

// Apply filters, sorting, and render products
function applyFilter(resetPage = true) {
  if (resetPage) currentPage = 1;

  let filtered = [...products];
  const searchKey = searchInput.value.toLowerCase();
  const categoryKey = categoryFilter.value;
  const stockKey = stockFilter.value;
  const sortKey = sortOption.value;

  // Search
  if (searchKey) {
    filtered = filtered.filter((p) => p.name.toLowerCase().includes(searchKey));
  }

  // Category
  if (categoryKey !== "all") {
    filtered = filtered.filter((p) => p.category === categoryKey);
  }

  // Stock
  if (stockKey === "low") {
    filtered = filtered.filter((p) => p.stock < 5);
  }

  // Sorting
  if (sortKey === "low-high") filtered.sort((a, b) => a.price - b.price);
  else if (sortKey === "high-low") filtered.sort((a, b) => b.price - a.price);
  else if (sortKey === "a-z")
    filtered.sort((a, b) => a.name.localeCompare(b.name));
  else if (sortKey === "z-a")
    filtered.sort((a, b) => b.name.localeCompare(a.name));

  renderProducts(filtered);
}

// Add/Edit product
let editingProductId = null;
let form = document.getElementById("add-product-form");

form.addEventListener("submit", (e) => {
  e.preventDefault();

  const name = document.getElementById("product-name").value.trim();
  const price = Number(document.getElementById("product-price").value);
  const stock = Number(document.getElementById("product-stock").value);
  const category = document.getElementById("product-category").value;

  if (!name || price <= 0 || stock < 0 || !category) {
    alert("Please fill all fields correctly!");
    return;
  }

  if (editingProductId !== null) {
    const product = products.find((p) => p.id === editingProductId);
    product.name = name;
    product.price = price;
    product.stock = stock;
    product.category = category;
    editingProductId = null;
  } else {
    const newId = products.length
      ? Math.max(...products.map((p) => p.id)) + 1
      : 1;
    products.push({ id: newId, name, price, stock, category });
  }

  localStorage.setItem("products", JSON.stringify(products));
  applyFilter(true);
  updateAnalytics();
  form.reset();
  document.getElementById("submit-btn").textContent = "Add Product";
});

// Update product
function updateProduct(id) {
  const product = products.find((p) => p.id === id);
  if (!product) return;

  document.getElementById("product-name").value = product.name;
  document.getElementById("product-price").value = product.price;
  document.getElementById("product-stock").value = product.stock;
  document.getElementById("product-category").value = product.category;

  editingProductId = id;
  document.getElementById("submit-btn").textContent = "Update Product";
}

// Delete product
function deleteProduct(id) {
  products = products.filter((p) => p.id !== id);
  localStorage.setItem("products", JSON.stringify(products));
  applyFilter(false);
  updateAnalytics();
}

// Product container click (edit/delete)
productContainer.addEventListener("click", (e) => {
  if (e.target.tagName === "BUTTON") {
    const id = Number(e.target.dataset.id);
    const action = e.target.dataset.action;
    if (action === "delete") deleteProduct(id);
    else updateProduct(id);
  }
});

// Analytics
function updateAnalytics() {
  let totalProducts = document.getElementById("total-products");
  let totalValue = document.getElementById("total-value");
  let totalOutOfStock = document.getElementById("out-of-stock");

  let priceSum = 0;
  let outOfStockCount = 0;

  for (let p of products) {
    if (p.stock === 0) outOfStockCount++;
    priceSum += p.price * p.stock;
  }

  totalProducts.innerText = products.length;
  totalValue.innerText = priceSum;
  totalOutOfStock.innerText = outOfStockCount;
}

// Fetch products from localStorage or dummy
function fetchProducts() {
  return new Promise((resolve) => {
    noProductsMessage.style.display = "block";
    noProductsMessage.innerText = "Loading products...";
    setTimeout(() => {
      const stored = localStorage.getItem("products");
      resolve(stored ? JSON.parse(stored) : dummyProducts);
    }, 1500);
  });
}

// Init
function init() {
  fetchProducts().then((data) => {
    products = data;
    applyFilter(true);
    updateAnalytics();

    searchInput.disabled = false;
    categoryFilter.disabled = false;
    stockFilter.disabled = false;
    sortOption.disabled = false;
  });
}

init();
