document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".changeQuant").forEach(form => {
        form.addEventListener("click", async function (event) {
            if (event.target.id === "plus" || event.target.id === "minus") {
                event.preventDefault();

                const quantitySpan = form.querySelector("#quantity-planned");
                const quantityInput = form.querySelector("#product-quantity");
                const availableQuantity = parseInt(form.querySelector("#quantity-available").textContent, 10);
                const currentQuantity = parseInt(quantitySpan.textContent, 10);
                const productId = form.querySelector("#productId").value;
                const change = event.target.id === "plus" ? 1 : -1;

                // проверка доступности изменений
                if ((change === 1 && currentQuantity < availableQuantity) ||
                    (change === -1 && currentQuantity > 0)) {

                    try {
                        const response = await fetch(`/items/${productId}/product/${change}`, {
                            method: "PUT",
                            headers: {
                                "Content-Type": "application/json"
                            }
                        });

                        if (response.ok) {
                            const data = await response.json();


                            quantitySpan.textContent = data.quantity;
                            quantityInput.value = data.quantity;
                            if (data.quantity === 0) {
                                const itemTable = document.getElementById("item" + productId);
                                if (itemTable) {
                                    itemTable.remove();
                                }
                            }


                            const cartTotalElem = document.querySelector("#cart-total");
                            const balanceElem = document.querySelector("#account-balance");

                            if (cartTotalElem && data.cartTotal !== undefined) {
                                cartTotalElem.textContent = data.cartTotal.toFixed(2);
                            }

                            if (balanceElem && data.balance !== undefined) {
                                balanceElem.textContent = data.balance.toFixed(2);
                            }


                            if (data.message) {
                                console.warn(data.message);
                            }

                        } else {
                            console.warn("Ошибка при обновлении количества товара");
                        }
                    } catch (error) {
                        console.error("Ошибка сети:", error);
                    }
                }
            }
        });
    });
});
