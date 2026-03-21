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
        <button data-id="${product.id}">Delete</button>
      </div>
    `;
  }

  productcontainer.innerHTML = html;
}

// initialize data
products = dummyProducts;

// initial render
renderProducts(products);
