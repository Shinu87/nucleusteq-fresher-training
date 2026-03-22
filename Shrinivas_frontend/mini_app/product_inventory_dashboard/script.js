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

let productcontainer = document.getElementById("product-container");
let noProductsMessage = document.getElementById("no-products-message");

function renderProducts(data) {
  productcontainer.innerHTML = "";

  // handle empty state
  if (data.length === 0) {
    noProductsMessage.style.display = "block";
    return;
  } else {
    noProductsMessage.style.display = "none";
  }

  let html = "";

  for (let product of data) {
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

  productcontainer.innerHTML = html;
}

// Function to filter products based on search input
function filterData(products, filterKey) {
  let filteredData = [];
  if (filterKey === "") {
    renderProducts(products);
    return;
  }
  for (let data of products) {
    if (data.name.toLowerCase().includes(filterKey.toLowerCase())) {
      filteredData.push(data);
    }
  }
  renderProducts(filteredData);
}

// Function to filter, sort, and render products
function applyfilter() {
  let searchKey = searchinput.value;
  let categoryKey = categoryfilter.value;
  let stockKey = stockfilter.value;
  let sortKey = sortoption.value;

  let filtered = [...products];

  // Apply search filter
  if (searchKey !== "") {
    filtered = filtered.filter((product) =>
      product.name.toLowerCase().includes(searchKey.toLowerCase()),
    );
  }

  // Apply category filter
  if (categoryKey !== "all") {
    filtered = filtered.filter((product) => product.category === categoryKey);
  }

  // Apply stock filter
  if (stockKey === "low") {
    filtered = filtered.filter((product) => product.stock < 5);
  }

  // Apply sorting
  if (sortKey === "low-high") {
    filtered.sort((a, b) => a.price - b.price);
  } else if (sortKey === "high-low") {
    filtered.sort((a, b) => b.price - a.price);
  } else if (sortKey === "a-z") {
    filtered.sort((a, b) => a.name.localeCompare(b.name));
  } else if (sortKey === "z-a") {
    filtered.sort((a, b) => b.name.localeCompare(a.name));
  }

  // Render the final filtered array
  renderProducts(filtered);
}

let searchinput = document.getElementById("search-input");
searchinput.addEventListener("input", () => {
  applyfilter();
});

let categoryfilter = document.getElementById("category-filter");
categoryfilter.addEventListener("change", () => {
  applyfilter();
});

let stockfilter = document.getElementById("stock-filter");
stockfilter.addEventListener("change", () => {
  applyfilter();
});

let sortoption = document.getElementById("sort-option");
sortoption.addEventListener("change", () => {
  applyfilter();
});

// Function to delete a product from the product grid
function deleteproduct(deleteid) {
  products = products.filter((p) => p.id !== deleteid);
  localStorage.setItem("products", JSON.stringify(products));
  renderProducts(products);
  updateAnalytics();
}

let editingProductId = null;

let form = document.getElementById("add-product-form");
// Handle form submit for Add or Edit
form.addEventListener("submit", (event) => {
  event.preventDefault();

  const name = document.getElementById("product-name").value.trim();
  const price = Number(document.getElementById("product-price").value);
  const stock = Number(document.getElementById("product-stock").value);
  const category = document.getElementById("product-category").value;

  if (!name || price <= 0 || stock < 0 || !category) {
    alert("Please fill all fields correctly!");
    return;
  }

  if (editingProductId !== null) {
    // EDIT existing product
    const product = products.find((p) => p.id === editingProductId);
    product.name = name;
    product.price = price;
    product.stock = stock;
    product.category = category;
    editingProductId = null;
  } else {
    // ADD new product
    const newId =
      products.length > 0 ? Math.max(...products.map((p) => p.id)) + 1 : 1;
    products.push({ id: newId, name, price, stock, category });
  }

  localStorage.setItem("products", JSON.stringify(products));
  renderProducts(products);
  updateAnalytics();
  form.reset();
});

function updateproduct(productid) {
  const product = products.find((p) => p.id === productid);
  if (!product) return;

  // Fill the form with product data
  document.getElementById("product-name").value = product.name;
  document.getElementById("product-price").value = product.price;
  document.getElementById("product-stock").value = product.stock;
  document.getElementById("product-category").value = product.category;

  editingProductId = productid;
  document.getElementById("submit-btn").textContent = "Update Product";
}

let productdelete = document.getElementById("product-container");
productdelete.addEventListener("click", (event) => {
  if (event.target.tagName === "BUTTON") {
    let id = Number(event.target.dataset.id);
    let action = event.target.dataset.action;
    if (action === "delete") {
      deleteproduct(id);
    } else {
      updateproduct(id);
    }
  }
});

// Function to update the inventory analytics dashboard
function updateAnalytics() {
  let totalproduct = document.getElementById("total-products");
  let totalvalue = document.getElementById("total-value");
  let totaloutofstock = document.getElementById("out-of-stock");
  let totalprice = 0;
  let countoutofstock = 0;
  for (let product of products) {
    if (product.stock == 0) {
      countoutofstock += 1;
    }
    totalprice += product.price * product.stock;
  }
  totalproduct.innerHTML = products.length;
  totalvalue.innerHTML = totalprice;
  totaloutofstock.innerHTML = countoutofstock;
}

function fetchproducts() {
  return new Promise((resolve) => {
    noProductsMessage.style.display = "block";
    noProductsMessage.innerHTML = "Loading products...";
    setTimeout(() => {
      const storedproduct = localStorage.getItem("products");
      if (storedproduct) {
        resolve(JSON.parse(storedproduct));
      } else {
        resolve(dummyProducts);
      }
    }, 1500);
  });
}

function init() {
  fetchproducts().then((data) => {
    products = data;
    renderProducts(products);
    updateAnalytics();
  });
}

init();
