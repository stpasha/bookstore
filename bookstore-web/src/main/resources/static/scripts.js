console.log("Скрипт загружен!");
document.addEventListener("DOMContentLoaded", function () {
            document.querySelectorAll("form").forEach(form => {
                form.addEventListener("click", function (event) {
                    if (event.target.id === "plus" || event.target.id === "minus") {
                        event.preventDefault();

                        let quantitySpan = form.querySelector("#quantity-planned");
                        let quantityInput = form.querySelector("#product-quantity");
                        let availableQuantity = parseInt(form.querySelector("#quantity-available").textContent, 10);
                        let currentQuantity = parseInt(quantitySpan.textContent, 10);

                        if (event.target.id === "plus" && currentQuantity < availableQuantity) {
                            currentQuantity++;
                        } else if (event.target.id === "minus" && currentQuantity > 0) {
                            currentQuantity--;
                        }

                        quantitySpan.textContent = currentQuantity;
                        quantityInput.value = currentQuantity;
                    }
                });
            });
        });